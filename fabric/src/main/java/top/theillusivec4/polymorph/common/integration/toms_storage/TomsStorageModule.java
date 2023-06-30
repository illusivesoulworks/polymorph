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

package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.PolymorphClient;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.toms_storage.AccessorContainerStorageTerminal;
import top.theillusivec4.polymorph.mixin.integration.toms_storage.AccessorTileEntityCraftingTerminal;

public class TomsStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerScreenHandler2BlockEntity(pContainer -> {
      if (pContainer instanceof ContainerCraftingTerminal) {
        return ((AccessorContainerStorageTerminal) pContainer).getTe();
      }
      return null;
    });
  }

  @Override
  public void registerBlockEntities() {
    PolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerBlockEntity2RecipeData(TileEntityCraftingTerminal.class,
        pTileEntity -> new CraftingTerminalRecipeData((TileEntityCraftingTerminal) pTileEntity));
  }

  @Override
  public void clientSetup() {
    PolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(handledScreen -> {
      if (handledScreen.getScreenHandler() instanceof ContainerCraftingTerminal) {
        return clientApi.findCraftingResultSlot(handledScreen)
            .map(slot -> new CraftingTerminalRecipesWidget(handledScreen, slot))
            .orElse(null);
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(BlockEntity tileEntity, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe && tileEntity instanceof TileEntityCraftingTerminal) {
      AccessorTileEntityCraftingTerminal te = (AccessorTileEntityCraftingTerminal) tileEntity;
      te.setCurrentRecipe((CraftingRecipe) recipe);
      te.callOnCraftingMatrixChanged();
      return true;
    }
    return false;
  }
}
