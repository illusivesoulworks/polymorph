/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.common.integration.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import com.tfar.craftingstation.client.CraftingStationScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CraftingStationModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen instanceof CraftingStationScreen &&
          containerScreen.getContainer() instanceof CraftingStationContainer) {
        return new PlayerRecipesWidget(containerScreen, containerScreen.getContainer().getSlot(0));
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer && recipe instanceof ICraftingRecipe) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      craftingStationContainer.lastRecipe = (ICraftingRecipe) recipe;
      craftingStationContainer.craftMatrix.onCraftMatrixChanged();
      return true;
    }
    return false;
  }
}
