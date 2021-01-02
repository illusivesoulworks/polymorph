/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.core.client.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.core.Polymorph;

public class FurnaceRecipeSelector
    extends AbstractRecipeSelector<Inventory, AbstractCookingRecipe> {

  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeSelector(HandledScreen<?> screen, FurnaceProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    ItemStack currentStack = this.provider.getInventory().getStack(0);

    if (!ItemStack.areItemsEqual(currentStack, lastStack)) {
      this.lastStack = currentStack;
      this.fetchRecipes();
    }
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {
    Polymorph.getLoader().getPacketVendor().sendSetRecipe(recipe.getId().toString());
  }

  @Override
  public void highlightRecipe(String recipe) {
    this.recipeSelectorGui.highlightButton(recipe);
  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes, World world, boolean refresh,
                         String selected) {

    if (refresh) {
      Set<RecipeOutput> recipeOutputs = new HashSet<>();
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutput(rec.craft(this.provider.getInventory()))));
    }
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;

    if (!recipes.isEmpty()) {
      this.highlightRecipe(selected.isEmpty() ? recipes.get(0).getId().toString() : selected);
    }

    RecipeSelectorManager.getPreferredRecipe().ifPresent(id -> {
      for (AbstractCookingRecipe recipe : recipes) {

        if (recipe.getId() == id) {
          RecipeSelectorManager.setPreferredRecipe(null);
          selectRecipe(recipe);
          return;
        }
      }
    });
  }

  @Override
  public void setRecipes(Set<String> recipeIds, World world, boolean refresh, String selected) {
    List<AbstractCookingRecipe> recipes = new ArrayList<>();
    recipeIds.forEach(
        id -> world.getRecipeManager().get(new Identifier(id)).ifPresent(recipe -> {
          if (recipe instanceof AbstractCookingRecipe) {
            recipes.add((AbstractCookingRecipe) recipe);
          }
        }));
    recipes.sort(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()));
    this.setRecipes(recipes, world, refresh, selected);
  }
}
