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

package top.theillusivec4.polymorph.common.integrations.silentgear;

import net.minecraft.inventory.container.Container;
import net.silentchaos512.gear.block.craftingstation.CraftingStationContainer;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class SilentGearModule extends CompatibilityModule {

  public void setup() {
    PolymorphApi.addProvider(CraftingStationContainer.class, CraftingStationProvider::new);
  }

  public static class CraftingStationProvider implements PolyProvider {

    private final CraftingStationContainer craftingStationContainer;

    public CraftingStationProvider(CraftingStationContainer craftingStationContainer) {
      this.craftingStationContainer = craftingStationContainer;
    }

    @Override
    public Container getContainer() {
      return this.craftingStationContainer;
    }

    @Override
    public int getXOffset() {
      return -4;
    }

    @Override
    public int getYOffset() {
      return -72;
    }
  }
}
