/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common.integrations.craftingcraft;

import javax.annotation.Nonnull;
import net.blay09.mods.craftingcraft.container.InventoryCraftingContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class CraftingCraftModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance()
        .addProvider(container -> {
          if (container.getClass() == InventoryCraftingContainer.class) {
            return new InventoryCraftingProvider((InventoryCraftingContainer) container);
          }
          return null;
        });
  }

  public static class InventoryCraftingProvider implements ICraftingProvider {

    InventoryCraftingContainer craftingContainer;

    public InventoryCraftingProvider(InventoryCraftingContainer craftingContainer) {
      this.craftingContainer = craftingContainer;
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return this.craftingContainer;
    }

    @Nonnull
    @Override
    public CraftingInventory getInventory() {
      return craftingContainer.getCraftMatrix();
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.craftingContainer.getSlot(0);
    }
  }
}
