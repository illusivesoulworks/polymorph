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
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import shadows.fastbench.FastBench;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.net.RecipeMessage;
import shadows.placebo.util.NetworkUtils;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.core.AccessorPlayerContainer;
import top.theillusivec4.polymorph.mixin.core.AccessorWorkbenchContainer;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe) {
      CraftingInventory inv = null;
      CraftResultInventory result = null;
      PlayerEntity player = null;
      ICraftingRecipe craftingRecipe = (ICraftingRecipe) recipe;

      if (container instanceof ContainerFastBench) {
        AccessorWorkbenchContainer accessor = (AccessorWorkbenchContainer) container;
        inv = accessor.getCraftSlots();
        result = accessor.getResultSlots();
        player = accessor.getPlayer();
      } else if (container instanceof PlayerContainer) {
        AccessorPlayerContainer accessor = (AccessorPlayerContainer) container;
        inv = accessor.getCraftSlots();
        result = accessor.getResultSlots();
        player = accessor.getOwner();
      }

      if (inv != null && result != null && player != null) {
        ItemStack stack = craftingRecipe.assemble(inv);

        if (!ItemStack.matches(stack, result.getItem(0))) {
          NetworkUtils.sendTo(FastBench.CHANNEL, new RecipeMessage(craftingRecipe, stack), player);
          result.setItem(0, stack);
          result.setRecipeUsed(recipe);
        }
      }
    }
    return false;
  }
}
