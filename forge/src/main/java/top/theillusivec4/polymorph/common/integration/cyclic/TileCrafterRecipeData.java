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

package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.common.capability.AbstractBlockEntityRecipeData;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorTileCrafter;

public class TileCrafterRecipeData extends AbstractBlockEntityRecipeData<TileCrafter> {

  private LazyOptional<IItemHandler> maybeGrid;

  public TileCrafterRecipeData(TileCrafter pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    if (this.maybeGrid == null) {
      this.maybeGrid = ((AccessorTileCrafter) this.getOwner()).getGridCap();

      if (this.maybeGrid == null) {
        return NonNullList.create();
      }
    }
    return this.maybeGrid.map(grid -> {
      NonNullList<ItemStack> result = NonNullList.withSize(9, ItemStack.EMPTY);
      for (int i = 0; i < 9; ++i) {
        result.set(i, grid.getStackInSlot(i));
      }
      return result;
    }).orElse(NonNullList.create());
  }
}
