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

package top.theillusivec4.polymorph.api.type;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;

public interface IPolyProvider<I extends IInventory> {

  default boolean isValid() {
    return true;
  }

  Container getContainer();

  @Nonnull
  I getInventory();

  @Nonnull
  Slot getOutputSlot();

  default int getXPos() {
    return getOutputSlot().xPos;
  }

  default int getYPos() {
    return getOutputSlot().yPos - 22;
  }

  IRecipeSelector<I, ? extends IRecipe<I>> createSelector(ContainerScreen<?> screen);
}