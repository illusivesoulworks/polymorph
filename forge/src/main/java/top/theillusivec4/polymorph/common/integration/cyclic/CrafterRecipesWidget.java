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

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class CrafterRecipesWidget extends PersistentRecipesWidget {

  private final ContainerCrafter container;

  public CrafterRecipesWidget(AbstractContainerScreen<?> screen, ContainerCrafter containerCrafter) {
    super(screen);
    this.container = containerCrafter;
  }

  @Override
  public Slot getOutputSlot() {
    int index = TileCrafter.GRID_NUM_COLS * TileCrafter.GRID_NUM_ROWS +
        TileCrafter.IO_NUM_COLS * TileCrafter.IO_NUM_ROWS * 2;
    return this.container.getSlot(index);
  }

  @Override
  public int getYPos() {
    return super.getYPos() + 3;
  }
}
