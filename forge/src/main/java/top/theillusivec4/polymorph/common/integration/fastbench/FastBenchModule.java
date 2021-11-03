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

package top.theillusivec4.polymorph.common.integration.fastbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import shadows.fastbench.FastBench;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.net.RecipeMessage;
import shadows.placebo.util.NetworkUtils;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.core.AccessorWorkbenchContainer;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof ContainerFastBench && recipe instanceof ICraftingRecipe) {
      AccessorWorkbenchContainer accessor = (AccessorWorkbenchContainer) container;
      CraftingInventory inv = accessor.getCraftMatrix();
      CraftResultInventory result = accessor.getCraftResult();
      PlayerEntity player = accessor.getPlayer();
      ICraftingRecipe craftingRecipe = (ICraftingRecipe) recipe;

      if (inv != null && result != null) {
        ItemStack stack = craftingRecipe.getCraftingResult(inv);

        if (!ItemStack.areItemStacksEqual(stack, result.getStackInSlot(0))) {
          NetworkUtils.sendTo(FastBench.CHANNEL, new RecipeMessage(craftingRecipe, stack), player);
          result.setInventorySlotContents(0, stack);
          result.setRecipeUsed(recipe);
        }
      }
    }
    return false;
  }
}
