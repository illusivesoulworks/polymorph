/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common.integration.fastbench;

import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.mixin.core.AccessorCraftingMenu;
import com.illusivesoulworks.polymorph.mixin.core.AccessorInventoryMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import shadows.fastbench.FastBench;
import shadows.fastbench.net.RecipeMessage;
import shadows.placebo.network.PacketDistro;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe craftingRecipe) {
      CraftingContainer inv = null;
      ResultContainer result = null;
      Player player = null;

      if (container instanceof CraftingMenu) {
        AccessorCraftingMenu accessor = (AccessorCraftingMenu) container;
        inv = accessor.getCraftSlots();
        result = accessor.getResultSlots();
        player = accessor.getPlayer();
      } else if (container instanceof InventoryMenu) {
        AccessorInventoryMenu accessor = (AccessorInventoryMenu) container;
        inv = accessor.getCraftSlots();
        result = accessor.getResultSlots();
        player = accessor.getOwner();
      }

      if (inv != null && result != null && player != null) {
        ItemStack stack = craftingRecipe.assemble(inv, player.level().registryAccess());

        // Some mods seem to be violating the non-null contract so this check is necessary
        // https://github.com/TheIllusiveC4/Polymorph/issues/163
        // noinspection ConstantConditions
        if (stack != null && !ItemStack.matches(stack, result.getItem(0))) {
          PacketDistro.sendTo(FastBench.CHANNEL, new RecipeMessage(craftingRecipe, stack), player);
          result.setItem(0, stack);
          result.setRecipeUsed(craftingRecipe);
        }
      }
    }
    return false;
  }
}
