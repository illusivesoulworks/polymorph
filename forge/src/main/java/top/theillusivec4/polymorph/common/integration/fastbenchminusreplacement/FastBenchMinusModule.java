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

package top.theillusivec4.polymorph.common.integration.fastbenchminusreplacement;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.core.AccessorPlayerContainer;
import top.theillusivec4.polymorph.mixin.core.AccessorWorkbenchContainer;

public class FastBenchMinusModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe) {
      CraftResultInventory result = null;

      if (container instanceof WorkbenchContainer) {
        AccessorWorkbenchContainer accessor = (AccessorWorkbenchContainer) container;
        result = accessor.getCraftResult();
      } else if (container instanceof PlayerContainer) {
        AccessorPlayerContainer accessor = (AccessorPlayerContainer) container;
        result = accessor.getCraftResult();
      }

      if (result != null) {
        result.setRecipeUsed(recipe);
      }
    }
    return false;
  }
}
