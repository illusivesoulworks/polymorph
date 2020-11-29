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

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.api.type.IPolyProvider;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;
import top.theillusivec4.polymorph.common.impl.PolymorphApiImpl;

public interface PolymorphApi {

  static PolymorphApi getInstance() {
    return PolymorphApiImpl.INSTANCE;
  }

  <T extends Container> void addProvider(Class<T> clazz,
                                         Function<T, IPolyProvider<?>> providerFunction);

  Optional<IPolyProvider<?>> getProvider(Container container);

  IRecipeSelector<CraftingInventory, ICraftingRecipe> createCraftingSelector(
      ContainerScreen<?> screen, ICraftingProvider provider);

  IRecipeSelector<IInventory, AbstractCookingRecipe> createFurnaceSelector(
      ContainerScreen<?> screen, IFurnaceProvider provider);
}
