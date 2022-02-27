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

package top.theillusivec4.polymorph.common.integration.extendedcrafting;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.common.capability.AbstractHighlightedRecipeData;
import top.theillusivec4.polymorph.mixin.integration.extendedcrafting.AccessorAutoTableTileEntity;

public class AutoTableRecipeData extends AbstractHighlightedRecipeData<AutoTableTileEntity> {

  public AutoTableRecipeData(AutoTableTileEntity pOwner) {
    super(pOwner);
  }

  @Override
  public void tick() {

    if (((AccessorAutoTableTileEntity) this.getOwner()).getIsGridChanged()) {
      this.sendRecipesListToListeners(this.isFailing() || this.isEmpty());
    }
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    NonNullList<ItemStack> input = NonNullList.create();
    BaseItemStackHandler stacks = this.getOwner().getInventory();

    for (int i = 0; i < stacks.getSlots() - 1; i++) {
      input.add(stacks.getStackInSlot(i));
    }
    return input;
  }
}
