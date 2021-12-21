package top.theillusivec4.polymorph.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.mixin.core.AccessorIngredient;
import top.theillusivec4.polymorph.mixin.core.AccessorSmithingRecipe;

public class PolymorphCommands {

  public static void setup() {
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> register(commandDispatcher));
  }

  private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    final int opPermissionLevel = 2;
    LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("polymorph")
        .requires(player -> player.hasPermissionLevel(opPermissionLevel));
    command.then(CommandManager.literal("conflicts")
        .executes(context -> findConflicts(context.getSource())));
    dispatcher.register(command);
  }

  private static int findConflicts(ServerCommandSource source) {
    CompletableFuture.runAsync(() -> {
      source.sendFeedback(new TranslatableText("commands.polymorph.conflicts.starting"), true);
      ServerWorld world = source.getWorld();
      RecipeManager recipeManager = world.getRecipeManager();
      List<String> output = new ArrayList<>();
      int count = 0;
      count += scanRecipes(RecipeType.CRAFTING, output, recipeManager, CraftingRecipeWrapper::new);
      count += scanRecipes(RecipeType.SMELTING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(RecipeType.BLASTING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(RecipeType.SMOKING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(RecipeType.SMITHING, output, recipeManager, SmithingRecipeWrapper::new);

      if (count > 0) {
        try {
          Files.write(Paths.get(FabricLoader.getInstance().getGameDir() + "/logs/conflicts.log"),
              output,
              StandardCharsets.UTF_8);
        } catch (IOException e) {
          PolymorphMod.LOGGER.error("Whoops! Something went wrong writing down your conflicts :(");
          e.printStackTrace();
        }
      }
      source.sendFeedback(new TranslatableText("commands.polymorph.conflicts.success", count),
          true);
    });
    return Command.SINGLE_SUCCESS;
  }

  private static <C extends Inventory, T extends Recipe<C>> int scanRecipes(RecipeType<T> pType,
                                                                            List<String> pOutput,
                                                                            RecipeManager pRecipeManager,
                                                                            Function<Recipe<?>, RecipeWrapper> pFactory) {
    Collection<RecipeWrapper> recipes =
        pRecipeManager.listAllOfType(pType).stream().map(pFactory).collect(Collectors.toList());
    List<Set<Identifier>> conflicts = new ArrayList<>();
    Set<Identifier> skipped = new TreeSet<>();
    Set<Identifier> processed = new HashSet<>();

    for (RecipeWrapper recipe : recipes) {
      Identifier id = recipe.getId();

      if (processed.contains(id)) {
        continue;
      }
      processed.add(id);

      if (recipe.getRecipe() instanceof SpecialCraftingRecipe) {
        skipped.add(id);
        continue;
      }
      Set<Identifier> currentGroup = new TreeSet<>();

      for (RecipeWrapper otherRecipe : recipes) {

        if (processed.contains(otherRecipe.getId())) {
          continue;
        }

        if (otherRecipe.conflicts(recipe)) {
          currentGroup.add(id);
          currentGroup.add(otherRecipe.getId());
          processed.add(otherRecipe.getId());
        }
      }

      if (!currentGroup.isEmpty()) {
        conflicts.add(currentGroup);
      }
    }
    pOutput.add("===================================================================");
    pOutput.add(
        Registry.RECIPE_TYPE.getKey(pType) + " recipe conflicts (" + conflicts.size() + ")");
    pOutput.add("===================================================================");
    pOutput.add("");
    int count = 1;

    for (Set<Identifier> conflict : conflicts) {
      StringJoiner joiner = new StringJoiner(", ");
      conflict.stream().map(Identifier::toString).forEach(joiner::add);
      pOutput.add(count + ": " + joiner);
      pOutput.add("");
      count++;
    }

    if (skipped.size() > 0) {
      pOutput.add("Skipped special recipes: ");

      for (Identifier resourceLocation : skipped) {
        pOutput.add(resourceLocation.toString());
      }
      pOutput.add("");
    }
    return conflicts.size();
  }

  private static class IngredientWrapper {

    private final Ingredient ingredient;

    private IngredientWrapper(Ingredient pIngredient) {
      this.ingredient = pIngredient;
    }

    public Ingredient getIngredient() {
      return this.ingredient;
    }

    public boolean matches(IngredientWrapper pIngredient) {

      if (pIngredient == null) {
        return false;
      }
      Ingredient otherIngredient = pIngredient.getIngredient();

      if (otherIngredient == null) {
        return false;
      } else if (otherIngredient == Ingredient.EMPTY) {
        return this.ingredient == Ingredient.EMPTY;
      } else {
        ItemStack[] stacks = getMatchingStacks(this.ingredient);

        for (ItemStack otherStack : getMatchingStacks(pIngredient.getIngredient())) {

          for (ItemStack stack : stacks) {

            if (ItemStack.areEqual(stack, otherStack)) {
              return true;
            }
          }
        }
        return false;
      }
    }
  }

  @SuppressWarnings("ConstantConditions")
  private static ItemStack[] getMatchingStacks(Ingredient ingredient) {
    return ((AccessorIngredient) (Object) ingredient).getStacks();
  }

  private static class RecipeWrapper {

    private final Recipe<?> recipe;
    private final List<IngredientWrapper> ingredients;

    private RecipeWrapper(Recipe<?> pRecipe) {
      this.recipe = pRecipe;
      this.ingredients = new ArrayList<>();

      for (Ingredient ingredient : this.recipe.getIngredients()) {
        IngredientWrapper wrapped = new IngredientWrapper(ingredient);
        this.ingredients.add(wrapped);
      }
    }

    public Recipe<?> getRecipe() {
      return this.recipe;
    }

    public Identifier getId() {
      return this.recipe.getId();
    }

    public List<IngredientWrapper> getIngredients() {
      return this.ingredients;
    }

    public boolean conflicts(RecipeWrapper pOther) {

      if (pOther == null) {
        return false;
      } else if (this.getId().equals(pOther.getId())) {
        return true;
      } else if (this.ingredients.size() != pOther.getIngredients().size()) {
        return false;
      } else {
        List<IngredientWrapper> otherIngredients = pOther.getIngredients();

        for (int i = 0; i < otherIngredients.size(); i++) {

          if (!otherIngredients.get(i).matches(this.getIngredients().get(i))) {
            return false;
          }
        }
        return true;
      }
    }
  }

  private static class SmithingRecipeWrapper extends RecipeWrapper {

    private SmithingRecipeWrapper(Recipe<?> pRecipe) {
      super(pRecipe);
    }

    @Override
    public boolean conflicts(RecipeWrapper pOther) {
      AccessorSmithingRecipe smithingRecipe = (AccessorSmithingRecipe) this.getRecipe();
      AccessorSmithingRecipe otherSmithingRecipe = (AccessorSmithingRecipe) pOther.getRecipe();
      IngredientWrapper baseWrapper = new IngredientWrapper(smithingRecipe.getBase());
      IngredientWrapper otherBaseWrapper = new IngredientWrapper(otherSmithingRecipe.getBase());
      IngredientWrapper additionWrapper = new IngredientWrapper(smithingRecipe.getAddition());
      IngredientWrapper otherAdditionWrapper =
          new IngredientWrapper(otherSmithingRecipe.getAddition());
      return super.conflicts(pOther) &&
          baseWrapper.matches(otherBaseWrapper) & additionWrapper.matches(otherAdditionWrapper);
    }
  }

  private static class CraftingRecipeWrapper extends RecipeWrapper {

    private final boolean shaped;

    private CraftingRecipeWrapper(Recipe<?> pRecipe) {
      super(pRecipe);
      this.shaped = pRecipe instanceof ShapedRecipe;
    }

    public boolean isShaped() {
      return this.shaped;
    }

    @Override
    public boolean conflicts(RecipeWrapper pOther) {
      return super.conflicts(pOther) && isSameShape((CraftingRecipeWrapper) pOther);
    }

    private boolean isSameShape(CraftingRecipeWrapper pOther) {

      if (this.shaped && pOther.isShaped()) {
        ShapedRecipe shaped = (ShapedRecipe) this.getRecipe();
        ShapedRecipe otherShaped = (ShapedRecipe) pOther.getRecipe();
        return shaped.getHeight() == otherShaped.getHeight() &&
            shaped.getWidth() == otherShaped.getWidth();
      }
      return true;
    }
  }
}
