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

package top.theillusivec4.polymorph.common.integrations.prettypipes;

import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class PrettyPipesModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance()
        .addProvider(container -> {
          if (container.getClass() == CraftingTerminalContainer.class) {
            return new CraftingTerminalProvider((CraftingTerminalContainer) container);
          }
          return null;
        });
  }

  public static class CraftingTerminalProvider implements ICraftingProvider {

    CraftingTerminalContainer craftingContainer;

    public CraftingTerminalProvider(CraftingTerminalContainer craftingContainer) {
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
      return craftingContainer.craftInventory;
    }

    @Override
    public int getXPos() {
      return this.getOutputSlot().xPos + 22;
    }

    @Override
    public int getYPos() {
      return this.getOutputSlot().yPos;
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.craftingContainer.getSlot(0);
    }
  }
}
