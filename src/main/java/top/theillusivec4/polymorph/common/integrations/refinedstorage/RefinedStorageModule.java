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

package top.theillusivec4.polymorph.common.integrations.refinedstorage;

import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.inventory.container.Container;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class RefinedStorageModule extends CompatibilityModule {

  public void setup() {
    PolymorphApi.addProvider(GridContainer.class, GridProvider::new);
  }

  public static class GridProvider implements PolyProvider {

    private final GridContainer gridContainer;

    public GridProvider(GridContainer gridContainer) {
      this.gridContainer = gridContainer;
    }

    @Override
    public Container getContainer() {
      return this.gridContainer;
    }

    @Override
    public int getXOffset() {
      return 21;
    }

    @Override
    public int getYOffset() {
      return -42;
    }
  }
}
