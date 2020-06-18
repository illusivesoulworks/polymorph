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

package top.theillusivec4.polymorph.common.provider;

import java.lang.reflect.Field;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;

public class InventoryProvider implements IProvider {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(PlayerContainer.class, "field_75181_e");

  private final PlayerContainer playerContainer;

  public InventoryProvider(PlayerContainer playerContainer) {
    this.playerContainer = playerContainer;
  }

  @Override
  public CraftingInventory getCraftingMatrix() {
    CraftingInventory craftingInventory = null;

    try {
      craftingInventory = (CraftingInventory) CRAFT_MATRIX.get(this.playerContainer);
    } catch (IllegalAccessException e) {
      Polymorph.LOGGER
          .error("Whoops, something went wrong while trying to retrieve the crafting matrix!");
    }
    return craftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.playerContainer.getSlot(this.playerContainer.getOutputSlot());
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
