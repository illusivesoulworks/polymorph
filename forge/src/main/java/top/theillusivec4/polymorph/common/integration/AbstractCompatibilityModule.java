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

package top.theillusivec4.polymorph.common.integration;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractCompatibilityModule {

  public void setup() {
    // NO-OP
  }

  public void clientSetup() {
    // NO-OP
  }

  public boolean selectRecipe(Container container, IRecipe<?> recipe) {
    return false;
  }

  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {
    return false;
  }

  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {
    return false;
  }
}
