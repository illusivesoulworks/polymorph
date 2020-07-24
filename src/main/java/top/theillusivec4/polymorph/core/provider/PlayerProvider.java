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

package top.theillusivec4.polymorph.core.provider;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.core.Polymorph;

public class PlayerProvider implements PolyProvider {

  private final PlayerScreenHandler playerHandler;

  public PlayerProvider(PlayerScreenHandler playerHandler) {
    this.playerHandler = playerHandler;
  }

  @Override
  public ScreenHandler getHandler() {
    return this.playerHandler;
  }

  @Override
  public CraftingInventory getCraftingInventory() {
    return Polymorph.getLoader().getAccessor().getCraftingInput(playerHandler);
  }

  @Override
  public Slot getOutputSlot() {
    return this.playerHandler.getSlot(this.playerHandler.getCraftingResultSlotIndex());
  }

  @Override
  public int getXOffset() {
    return 66;
  }

  @Override
  public int getYOffset() {
    return -74;
  }
}
