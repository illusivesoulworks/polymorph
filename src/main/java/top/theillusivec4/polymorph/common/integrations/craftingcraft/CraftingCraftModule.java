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

package top.theillusivec4.polymorph.common.integrations.craftingcraft;

import net.blay09.mods.craftingcraft.container.InventoryCraftingContainer;
import net.blay09.mods.craftingcraft.container.PortableCraftingContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class CraftingCraftModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.addProvider(InventoryCraftingContainer.class, InventoryCraftingProvider::new);
    PolymorphApi.addProvider(PortableCraftingContainer.class, PortableCraftingProvider::new);
  }

  public static class InventoryCraftingProvider implements PolyProvider {

    InventoryCraftingContainer craftingContainer;

    public InventoryCraftingProvider(InventoryCraftingContainer craftingContainer) {
      this.craftingContainer = craftingContainer;
    }

    @Override
    public Container getContainer() {
      return this.craftingContainer;
    }

    @Override
    public CraftingInventory getCraftingInventory() {
      return craftingContainer.getCraftMatrix();
    }

    @Override
    public int getXOffset() {
      return 28;
    }

    @Override
    public int getYOffset() {
      return -49;
    }
  }

  public static class PortableCraftingProvider implements PolyProvider {

    PortableCraftingContainer portableContainer;

    public PortableCraftingProvider(PortableCraftingContainer portableContainer) {
      this.portableContainer = portableContainer;
    }

    @Override
    public Container getContainer() {
      return this.portableContainer;
    }

    @Override
    public int getXOffset() {
      return 36;
    }

    @Override
    public int getYOffset() {
      return -72;
    }
  }
}
