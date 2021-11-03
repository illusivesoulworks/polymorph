/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.loading.FMLPaths;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.mixin.core.AccessorSmithingRecipe;

public class PolymorphCommands {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    final int opPermissionLevel = 2;
    LiteralArgumentBuilder<CommandSource> command = Commands.literal("polymorph")
        .requires(player -> player.hasPermission(opPermissionLevel));
    command.then(
        Commands.literal("conflicts").executes(context -> findConflicts(context.getSource())));
    dispatcher.register(command);
  }

  private static int findConflicts(CommandSource source) {
    CompletableFuture.runAsync(() -> {
      source.sendSuccess(new TranslationTextComponent("commands.polymorph.conflicts.starting"),
          true);
      ServerWorld world = source.getLevel();
      RecipeManager recipeManager = world.getRecipeManager();
      List<String> output = new ArrayList<>();
      int count = 0;
      count += scanRecipes(IRecipeType.CRAFTING, output, recipeManager, CraftingRecipeWrapper::new);
      count += scanRecipes(IRecipeType.SMELTING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(IRecipeType.BLASTING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(IRecipeType.SMOKING, output, recipeManager, RecipeWrapper::new);
      count += scanRecipes(IRecipeType.SMITHING, output, recipeManager, SmithingRecipeWrapper::new);

      if (count > 0) {
        try {
          Files.write(Paths.get(FMLPaths.GAMEDIR.get() + "/logs/conflicts.log"), output,
              StandardCharsets.UTF_8);
        } catch (IOException e) {
          PolymorphMod.LOGGER.error("Whoops! Something went wrong writing down your conflicts :(");
          e.printStackTrace();
        }
      }
      source.sendSuccess(
          new TranslationTextComponent("commands.polymorph.conflicts.success", count),
          true);
    });
    return Command.SINGLE_SUCCESS;
  }

  private static <C extends IInventory, T extends IRecipe<C>> int scanRecipes(IRecipeType<T> pType,
                                                                              List<String> pOutput,
                                                                              RecipeManager pRecipeManager,
                                                                              Function<IRecipe<?>, RecipeWrapper> pFactory) {
    Collection<RecipeWrapper> recipes =
        pRecipeManager.getAllRecipesFor(pType).stream().map(pFactory).collect(Collectors.toList());
    List<Set<ResourceLocation>> conflicts = new ArrayList<>();
    Set<ResourceLocation> skipped = new TreeSet<>();
    Set<ResourceLocation> processed = new HashSet<>();

    for (RecipeWrapper recipe : recipes) {
      ResourceLocation id = recipe.getId();

      if (processed.contains(id)) {
        continue;
      }
      processed.add(id);

      if (recipe.getRecipe() instanceof SpecialRecipe) {
        skipped.add(id);
        continue;
      }
      Set<ResourceLocation> currentGroup = new TreeSet<>();

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

    for (Set<ResourceLocation> conflict : conflicts) {
      StringJoiner joiner = new StringJoiner(", ");
      conflict.stream().map(ResourceLocation::toString).forEach(joiner::add);
      pOutput.add(count + ": " + joiner);
      pOutput.add("");
      count++;
    }

    if (skipped.size() > 0) {
      pOutput.add("Skipped special recipes: ");

      for (ResourceLocation resourceLocation : skipped) {
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
        ItemStack[] stacks = this.ingredient.getItems();

        for (ItemStack otherStack : pIngredient.getIngredient().getItems()) {

          for (ItemStack stack : stacks) {

            if (ItemStack.matches(stack, otherStack)) {
              return true;
            }
          }
        }
        return false;
      }
    }
  }

  private static class RecipeWrapper {

    private final IRecipe<?> recipe;
    private final List<IngredientWrapper> ingredients;

    private RecipeWrapper(IRecipe<?> pRecipe) {
      this.recipe = pRecipe;
      this.ingredients = new ArrayList<>();

      for (Ingredient ingredient : this.recipe.getIngredients()) {
        IngredientWrapper wrapped = new IngredientWrapper(ingredient);
        this.ingredients.add(wrapped);
      }
    }

    public IRecipe<?> getRecipe() {
      return this.recipe;
    }

    public ResourceLocation getId() {
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

    private SmithingRecipeWrapper(IRecipe<?> pRecipe) {
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

    private CraftingRecipeWrapper(IRecipe<?> pRecipe) {
      super(pRecipe);
      this.shaped = pRecipe instanceof IShapedRecipe;
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
        IShapedRecipe<?> shaped = (IShapedRecipe<?>) this.getRecipe();
        IShapedRecipe<?> otherShaped = (IShapedRecipe<?>) pOther.getRecipe();
        return shaped.getRecipeHeight() == otherShaped.getRecipeHeight() &&
            shaped.getRecipeWidth() == otherShaped.getRecipeWidth();
      }
      return true;
    }
  }
}
