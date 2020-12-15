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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.ScreenHandler;
import top.theillusivec4.polymorph.api.type.CraftingProvider;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.api.type.PolyProvider;
import top.theillusivec4.polymorph.api.type.RecipeSelector;
import top.theillusivec4.polymorph.loader.impl.PolymorphApiImpl;

public interface PolymorphApi {

  static PolymorphApi getInstance() {
    return PolymorphApiImpl.INSTANCE;
  }

  void addProvider(Function<ScreenHandler, PolyProvider<?, ?>> providerFunction);

  Optional<PolyProvider<?, ?>> getProvider(ScreenHandler screenHandler);

  RecipeSelector<CraftingInventory, CraftingRecipe> createCraftingSelector(
      HandledScreen<?> screen, CraftingProvider provider);

  RecipeSelector<Inventory, AbstractCookingRecipe> createFurnaceSelector(
      HandledScreen<?> screen, FurnaceProvider provider);
}
