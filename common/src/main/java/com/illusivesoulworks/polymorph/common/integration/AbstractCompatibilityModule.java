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

package com.illusivesoulworks.polymorph.common.integration;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractCompatibilityModule {

  public void setup() {
    // NO-OP
  }

  public void clientSetup() {
    // NO-OP
  }

  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {
    return false;
  }

  public boolean selectRecipe(BlockEntity tileEntity, Recipe<?> recipe) {
    return false;
  }

  public boolean openContainer(AbstractContainerMenu container, ServerPlayer serverPlayerEntity) {
    return false;
  }
}
