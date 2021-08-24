package top.theillusivec4.polymorph.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.mixin.core.AccessorIngredient;

public class PolymorphCommands {

  public static void setup() {
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> register(commandDispatcher));
  }

  private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    final int opPermissionLevel = 4;
    LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("polymorph")
        .requires(player -> player.hasPermissionLevel(opPermissionLevel));

    command.then(CommandManager.literal("conflicts")
        .executes(context -> findConflicts(context.getSource())));

    dispatcher.register(command);
  }

  private static int findConflicts(ServerCommandSource source) {
    ServerWorld world = source.getWorld();
    List<String> lines = new ArrayList<>();
    Set<String> processed = new HashSet<>();
    Collection<Recipe<?>> recipes = world.getRecipeManager().values();
    RecipeMatcher recipeItemHelper = new RecipeMatcher();
    IntArrayList first = new IntArrayList();
    IntArrayList second = new IntArrayList();
    AtomicInteger conflictCount = new AtomicInteger();
    source.sendFeedback(new TranslatableText("commands.polymorph.conflicts.starting"), true);

    try {
      recipes.forEach(recipe -> {
        String id = recipe.getId().toString();
        List<String> conflicts = new ArrayList<>();

        if (!processed.contains(id)) {
          processed.add(id);
          recipes.forEach(otherRecipe -> {
            String otherId = otherRecipe.getId().toString();

            // Algorithm based on the one from NoMoreRecipeConflict by stimmedCow & GotoLink
            // License is Public Domain
            if (!processed.contains(otherId) && !ItemStack
                .areEqual(otherRecipe.getOutput(), recipe.getOutput()) &&
                recipe.getType() == RecipeType.CRAFTING && recipe.getType() == otherRecipe
                .getType() && !recipe.isIgnoredInRecipeBook() &&
                recipe.isIgnoredInRecipeBook() == otherRecipe.isIgnoredInRecipeBook()
                && areSameShape(recipe, otherRecipe)) {
              recipeItemHelper.clear();
              recipe.getIngredients().forEach(ingredient -> {
                ItemStack[] stacks = ((AccessorIngredient) (Object) ingredient).getMatchingStacks();

                for (ItemStack matchingStack : stacks) {
                  recipeItemHelper.addInput(matchingStack);
                }
              });
              second.clear();

              if (recipeItemHelper.match(recipe, second)) {
                recipeItemHelper.clear();
                for (Ingredient ingredient : otherRecipe.getIngredients()) {
                  ItemStack[] stacks =
                      ((AccessorIngredient) (Object) ingredient).getMatchingStacks();

                  for (ItemStack matchingStack : stacks) {
                    recipeItemHelper.addInput(matchingStack);
                  }
                }
                first.clear();

                if (recipeItemHelper.match(otherRecipe, first) && first.equals(second)) {
                  processed.add(otherId);
                  conflicts.add(otherId);
                }
              }
            }
          });
        }

        if (!conflicts.isEmpty()) {
          conflictCount.addAndGet(conflicts.size());
          lines.add("Conflicts with " + id + ":");
          lines.addAll(conflicts);
          lines.add("");
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!lines.isEmpty()) {
      try {
        Files.write(Paths.get(FabricLoader.getInstance().getGameDir() + "/logs/conflicts.log"),
            lines, StandardCharsets.UTF_8);
      } catch (IOException e) {
        PolymorphMod.LOGGER.error("Whoops! Something went wrong writing down your conflicts :(");
      }
    }
    source.sendFeedback(
        new TranslatableText("commands.polymorph.conflicts.success", conflictCount.get()), true);
    return Command.SINGLE_SUCCESS;
  }

  private static boolean areSameShape(Recipe<?> recipe1, Recipe<?> recipe2) {

    if (recipe1 instanceof ShapedRecipe && recipe2 instanceof ShapedRecipe) {
      ShapedRecipe shapedRecipe1 = (ShapedRecipe) recipe1;
      ShapedRecipe shapedRecipe2 = (ShapedRecipe) recipe2;
      return shapedRecipe1.getHeight() == shapedRecipe2.getHeight()
          && shapedRecipe1.getWidth() == shapedRecipe2.getWidth();
    }
    return true;
  }
}
