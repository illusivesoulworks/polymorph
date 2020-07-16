/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;

public interface PolyProvider {

  Container getContainer();

  default CraftingInventory getCraftingInventory() {

    for (Slot slot : this.getContainer().inventorySlots) {

      if (slot.inventory instanceof CraftingInventory) {
        return (CraftingInventory) slot.inventory;
      }
    }
    return null;
  }

  default Slot getOutputSlot() {

    for (Slot slot : this.getContainer().inventorySlots) {

      if (slot.inventory instanceof CraftResultInventory) {
        return slot;
      }
    }
    return this.getContainer().inventorySlots.get(0);
  }

  int getXOffset();

  int getYOffset();

  default void transfer(PlayerEntity playerIn, ICraftingRecipe recipe) {
    Container container = getContainer();
    Slot slot = getOutputSlot();
    CraftingInventory inventory = getCraftingInventory();

    if (inventory != null && slot != null) {
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
  }
}