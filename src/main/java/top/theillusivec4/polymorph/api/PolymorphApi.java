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
import java.util.function.Function;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class PolymorphApi {

  private static final Map<Class<? extends Container>, Function<? extends Container, IProvider>> providerFunctions = new HashMap<>();

  public static <T extends Container> void addProvider(Class<T> clazz,
      Function<T, IProvider> providerFunction) {
    providerFunctions.put(clazz, providerFunction);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Container> Optional<IProvider> getProvider(T container) {
    Function<T, IProvider> providerFunction = (Function<T, IProvider>) providerFunctions
        .get(container.getClass());
    return providerFunction != null ? Optional.of(providerFunction.apply(container))
        : Optional.empty();
  }

  public interface IProvider {

    Container getContainer();

    default CraftingInventory getCraftingInventory() {

      for (Slot slot : this.getContainer().inventorySlots) {

        if (slot.inventory instanceof CraftingInventory) {
          return (CraftingInventory) slot.inventory;
        }
      }
      return null;
    }

    default Slot getOutputSlot() {

      for (Slot slot : this.getContainer().inventorySlots) {

        if (slot.inventory instanceof CraftResultInventory) {
          return slot;
        }
      }
      return this.getContainer().inventorySlots.get(0);
    }

    int getXOffset();

    int getYOffset();
  }
}
