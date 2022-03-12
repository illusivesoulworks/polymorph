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

package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import top.theillusivec4.polymorph.common.capability.AbstractBlockEntityRecipeData;
import top.theillusivec4.polymorph.mixin.integration.tconstruct.AccessorCraftingStationBlockEntity;

public class CraftingStationRecipeData
    extends AbstractBlockEntityRecipeData<CraftingStationBlockEntity> {

  private CraftingContainer craftingInventory;

  public CraftingStationRecipeData(CraftingStationBlockEntity pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {

    if (this.craftingInventory == null) {
      this.craftingInventory =
          ((AccessorCraftingStationBlockEntity) this.getOwner()).getCraftingInventory();
    }

    if (this.craftingInventory != null) {

      NonNullList<ItemStack> input =
          NonNullList.withSize(this.craftingInventory.getContainerSize(), ItemStack.EMPTY);

      for (int i = 0; i < this.craftingInventory.getContainerSize(); i++) {
        input.set(i, this.craftingInventory.getItem(i));
      }
      return input;
    }
    return NonNullList.create();
  }
}
