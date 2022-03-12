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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.tconstruct.tables.block.entity.inventory.CraftingContainerWrapper;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class TinkersConstructModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof CraftingStationBlockEntity) {
        return new CraftingStationRecipeData((CraftingStationBlockEntity) pTileEntity);
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen.getMenu() instanceof CraftingStationContainerMenu craftingStationContainer) {
        return new CraftingStationRecipesWidget(containerScreen,
            craftingStationContainer.slots.get(9));
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (container instanceof CraftingStationContainerMenu craftingStationContainer &&
        recipe instanceof CraftingRecipe) {
      CraftingStationBlockEntity tileEntity = craftingStationContainer.getTile();

      if (tileEntity != null) {
        tileEntity.updateRecipe((CraftingRecipe) recipe);
        tileEntity.syncToRelevantPlayers(tileEntity::syncRecipe);
      }
      return true;
    }
    return false;
  }

  public static void calcResult(InventoryBlockEntity pTileEntity, @Nullable Player pPlayer,
                                @Nullable CraftingRecipe pRecipe,
                                CraftingContainerWrapper pCraftingInventory) {

    if (pPlayer instanceof ServerPlayer) {
      PolymorphApi.common().getRecipeData(pTileEntity).ifPresent(recipeData -> {
        if (pRecipe != null && pRecipe.matches(pCraftingInventory, pTileEntity.getLevel())) {
          recipeData.setFailing(false);
        }
      });
    }
  }
}
