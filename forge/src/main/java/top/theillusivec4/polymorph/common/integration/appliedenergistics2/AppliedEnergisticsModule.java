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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermMenu;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermMenu;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen.getMenu() instanceof CraftingTermMenu) {

        for (Slot inventorySlot : pContainerScreen.getMenu().slots) {

          if (inventorySlot instanceof CraftingTermSlot) {
            return new PlayerRecipesWidget(pContainerScreen, inventorySlot);
          }
        }
      } else if (pContainerScreen.getMenu() instanceof PatternEncodingTermMenu) {

        for (Slot inventorySlot : pContainerScreen.getMenu().slots) {

          if (inventorySlot instanceof PatternTermSlot) {
            return new PatternTermRecipesWidget(pContainerScreen,
                (PatternEncodingTermMenu) pContainerScreen.getMenu(), inventorySlot);
          }
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe) {

      if (container instanceof CraftingTermMenu) {
        ((AccessorCraftingTermMenu) container).setCurrentRecipe((CraftingRecipe) recipe);
        container.slotsChanged(((CraftingTermMenu) container).getPlayerInventory());
        return true;
      } else if (container instanceof PatternEncodingTermMenu) {
        ((AccessorPatternTermMenu) container).setCurrentRecipe((CraftingRecipe) recipe);
        ((AccessorPatternTermMenu) container).callGetAndUpdateOutput();
        return true;
      }
    }
    return false;
  }
}
