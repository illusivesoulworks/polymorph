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

import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.PatternTermContainer;
import appeng.container.slot.CraftingTermSlot;
import appeng.container.slot.PatternTermSlot;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermContainer;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphNetwork.register(CPacketCallUpdate.class, CPacketCallUpdate::encode,
        CPacketCallUpdate::decode, CPacketCallUpdate::handle);
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen.getMenu() instanceof CraftingTermContainer) {

        for (Slot inventorySlot : pContainerScreen.getMenu().slots) {

          if (inventorySlot instanceof CraftingTermSlot) {
            return new PlayerRecipesWidget(pContainerScreen, inventorySlot);
          }
        }
      } else if (pContainerScreen.getMenu() instanceof PatternTermContainer) {

        for (Slot inventorySlot : pContainerScreen.getMenu().slots) {

          if (inventorySlot instanceof PatternTermSlot) {
            return new PatternTermRecipesWidget(pContainerScreen,
                (PatternTermContainer) pContainerScreen.getMenu(), inventorySlot);
          }
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe) {

      if (container instanceof CraftingTermContainer) {
        ((AccessorCraftingTermContainer) container).setCurrentRecipe((ICraftingRecipe) recipe);
        container.slotsChanged(((CraftingTermContainer) container).getPlayerInventory());
        return true;
      } else if (container instanceof PatternTermContainer) {
        ((AccessorPatternTermContainer) container).setCurrentRecipe((ICraftingRecipe) recipe);
        ((AccessorPatternTermContainer) container).callGetAndUpdateOutput();
        return true;
      }
    }
    return false;
  }
}
