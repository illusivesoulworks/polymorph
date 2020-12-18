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

package top.theillusivec4.polymorph.api.type;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;

public interface ICraftingProvider extends IPolyProvider<CraftingInventory, ICraftingRecipe> {

  default void transfer(PlayerEntity playerIn, ICraftingRecipe recipe) {
    Container container = getContainer();
    Slot slot = getOutputSlot();
    CraftingInventory inventory = getInventory();
    ItemStack itemstack = container.transferStackInSlot(playerIn, slot.slotNumber);

    if (recipe.matches(inventory, playerIn.world)) {
      slot.putStack(recipe.getCraftingResult(inventory));

      while (!itemstack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemstack)) {
        itemstack = container.transferStackInSlot(playerIn, slot.slotNumber);

        if (recipe.matches(inventory, playerIn.world)) {
          slot.putStack(recipe.getCraftingResult(inventory));
        }
      }
    }
  }

  @Nonnull
  @Override
  default IRecipeSelector<CraftingInventory, ICraftingRecipe> createSelector(
      ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().createCraftingSelector(screen, this);
  }

  @Nonnull
  @Override
  default List<? extends ICraftingRecipe> getRecipes(World world, RecipeManager recipeManager) {
    return recipeManager.getRecipes(IRecipeType.CRAFTING, this.getInventory(), world);
  }
}
