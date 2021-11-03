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

package top.theillusivec4.polymorph.common.integration.tconstruct;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class TinkersConstructModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof CraftingStationTileEntity) {
        return new CraftingStationRecipeData((CraftingStationTileEntity) pTileEntity);
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen.getMenu() instanceof CraftingStationContainer) {
        CraftingStationContainer craftingStationContainer =
            (CraftingStationContainer) containerScreen.getMenu();
        return new CraftingStationRecipesWidget(containerScreen,
            craftingStationContainer.slots.get(9));
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer && recipe instanceof ICraftingRecipe) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      CraftingStationTileEntity tileEntity = craftingStationContainer.getTile();

      if (tileEntity != null) {
        tileEntity.updateRecipe((ICraftingRecipe) recipe);
        tileEntity.syncToRelevantPlayers(tileEntity::syncRecipe);
      }
      return true;
    }
    return false;
  }

  public static void calcResult(InventoryTileEntity pTileEntity, @Nullable PlayerEntity pPlayer,
                                @Nullable ICraftingRecipe pRecipe,
                                CraftingInventory pCraftingInventory) {

    if (pPlayer instanceof ServerPlayerEntity) {
      PolymorphApi.common().getRecipeData(pTileEntity).ifPresent(recipeData -> {
        if (pRecipe != null && pRecipe.matches(pCraftingInventory, pTileEntity.getLevel())) {
          recipeData.setFailing(false);
        }
      });
    }
  }
}
