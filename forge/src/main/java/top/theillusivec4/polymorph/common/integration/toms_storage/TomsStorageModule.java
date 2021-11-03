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
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.toms_storage.AccessorContainerStorageTerminal;
import top.theillusivec4.polymorph.mixin.integration.toms_storage.AccessorTileEntityCraftingTerminal;

public class TomsStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof TileEntityCraftingTerminal) {
        return new CraftingTerminalRecipeData((TileEntityCraftingTerminal) pTileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(pContainer -> {
      if (pContainer instanceof ContainerCraftingTerminal) {
        return ((AccessorContainerStorageTerminal) pContainer).getTe();
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(containerScreen -> {
      if (containerScreen.getContainer() instanceof ContainerCraftingTerminal) {
        return clientApi.findCraftingResultSlot(containerScreen)
            .map(slot -> new CraftingTerminalRecipesWidget(containerScreen, slot))
            .orElse(null);
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe && tileEntity instanceof TileEntityCraftingTerminal) {
      AccessorTileEntityCraftingTerminal te = (AccessorTileEntityCraftingTerminal) tileEntity;
      te.setCurrentRecipe((ICraftingRecipe) recipe);
      te.callOnCraftingMatrixChanged();
      return true;
    }
    return false;
  }
}
