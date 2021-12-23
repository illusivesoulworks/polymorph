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

package top.theillusivec4.polymorph.common.integration.refinedstorageaddons;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.refinedstorage.AccessorGrid;

public class RefinedStorageAddonsModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe && container instanceof GridContainerMenu) {
      IGrid grid = ((GridContainerMenu) container).getGrid();

      if (grid instanceof WirelessCraftingGrid) {
        ((AccessorGrid) grid).setCurrentRecipe((CraftingRecipe) recipe);
        grid.onCraftingMatrixChanged();
        return true;
      }
    }
    return false;
  }
}
