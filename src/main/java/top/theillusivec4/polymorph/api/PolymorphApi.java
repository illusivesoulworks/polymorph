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

package top.theillusivec4.polymorph.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class PolymorphApi {

  private static final Map<Class<? extends Container>, IProvider<? extends Container>> providers = new HashMap<>();

  public static void addProvider(Class<? extends Container> clazz,
      IProvider<? extends Container> provider) {
    providers.put(clazz, provider);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Container> Optional<IProvider<T>> getProvider(T container) {
    IProvider<?> provider = providers.get(container.getClass());
    return provider != null ? Optional.of((IProvider<T>) provider) : Optional.empty();
  }

  public interface IProvider<T extends Container> {

    CraftingInventory getCraftingMatrix(T container);

    Slot getOutputSlot(T container);

    int getXOffset();

    int getYOffset();
  }
}
