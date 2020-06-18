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

package top.theillusivec4.polymorph.common.integrations.fastbench;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import shadows.fastbench.gui.ContainerFastBench;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.provider.WorkbenchProvider;

public class FastWorkbenchModule {

  public static void setup() {
    PolymorphApi.addProvider(ContainerFastBench.class, WorkbenchProvider::new);
  }

  public static void setLastRecipe(Container container, IRecipe<CraftingInventory> recipe) {

    if (container instanceof ContainerFastBench) {
      ((ContainerFastBench) container).lastRecipe = recipe;
    }
  }
}
