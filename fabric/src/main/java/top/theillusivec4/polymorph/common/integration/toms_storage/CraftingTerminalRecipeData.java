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

package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import top.theillusivec4.polymorph.common.component.AbstractBlockEntityRecipeData;

public class CraftingTerminalRecipeData
    extends AbstractBlockEntityRecipeData<TileEntityCraftingTerminal> {

  public CraftingTerminalRecipeData(TileEntityCraftingTerminal pOwner) {
    super(pOwner);
  }

  @Override
  protected DefaultedList<ItemStack> getInput() {
    CraftingInventory craftingInventory = this.getOwner().getCraftingInv();

    if (craftingInventory != null) {
      DefaultedList<ItemStack> stacks =
          DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

      for (int i = 0; i < craftingInventory.size(); i++) {
        stacks.set(i, craftingInventory.getStack(i));
      }
      return stacks;
    }
    return DefaultedList.of();
  }
}
