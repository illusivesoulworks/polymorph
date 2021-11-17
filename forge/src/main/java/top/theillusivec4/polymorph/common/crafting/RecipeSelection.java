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

package top.theillusivec4.polymorph.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public class RecipeSelection {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPlayerRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer) {
    return getPlayerRecipe(pType, pInventory, pWorld, pPlayer, new ArrayList<>());
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPlayerRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer, List<T> pRecipes) {
    LazyOptional<IPlayerRecipeData> maybeData = PolymorphApi.common().getRecipeData(pPlayer);
    return getRecipe(pType, pInventory, pWorld, maybeData, pRecipes);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getStackRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, ItemStack pStack) {
    LazyOptional<IStackRecipeData> maybeData = PolymorphApi.common().getRecipeData(pStack);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getTileEntityRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, TileEntity pTileEntity) {
    LazyOptional<ITileEntityRecipeData> maybeData =
        PolymorphApi.common().getRecipeData(pTileEntity);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  private static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, LazyOptional<? extends IRecipeData<?>> pOpt,
      List<T> pRecipes) {

    if (pOpt.isPresent()) {
      return pOpt.map(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, pRecipes))
          .orElse(Optional.empty());
    } else {
      return pWorld.getRecipeManager().getRecipes(pType, pInventory, pWorld).stream().findFirst();
    }
  }
}
