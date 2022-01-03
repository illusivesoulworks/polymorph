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

package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.CraftingTermSlot;
import appeng.menu.slot.PatternTermSlot;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermContainer;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pHandledScreen -> {
      if (pHandledScreen.getScreenHandler() instanceof CraftingTermMenu) {

        for (Slot inventorySlot : pHandledScreen.getScreenHandler().slots) {

          if (inventorySlot instanceof CraftingTermSlot) {
            return new PlayerRecipesWidget(pHandledScreen, inventorySlot);
          }
        }
      } else if (pHandledScreen.getScreenHandler() instanceof PatternEncodingTermMenu) {

        for (Slot inventorySlot : pHandledScreen.getScreenHandler().slots) {

          if (inventorySlot instanceof PatternTermSlot) {
            return new PatternTermRecipesWidget(pHandledScreen,
                (PatternEncodingTermMenu) pHandledScreen.getScreenHandler(), inventorySlot);
          }
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(ScreenHandler container, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe) {

      if (container instanceof CraftingTermMenu) {
        ((AccessorCraftingTermContainer) container).setCurrentRecipe((CraftingRecipe) recipe);
        container.onContentChanged(((CraftingTermMenu) container).getPlayerInventory());
        return true;
      } else if (container instanceof PatternEncodingTermMenu) {
        ((AccessorPatternTermContainer) container).setCurrentRecipe((CraftingRecipe) recipe);
        ((AccessorPatternTermContainer) container).callGetAndUpdateOutput();
        return true;
      }
    }
    return false;
  }
}
