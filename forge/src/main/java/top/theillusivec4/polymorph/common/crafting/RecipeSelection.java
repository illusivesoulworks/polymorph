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
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class RecipeSelection {

  public static <T extends Recipe<C>, C extends Container> Optional<T> getPlayerRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, Player pPlayer) {
    return getPlayerRecipe(pType, pInventory, pWorld, pPlayer, new ArrayList<>());
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getPlayerRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, Player pPlayer, List<T> pRecipes) {
    return PolymorphApi.common().getRecipeData(pPlayer)
        .map(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, pRecipes))
        .orElse(Optional.empty());
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getStackRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, ItemStack pStack) {
    return PolymorphApi.common().getRecipeData(pStack)
        .map(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, new ArrayList<>()))
        .orElse(Optional.empty());
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getTileEntityRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, BlockEntity pTileEntity) {
    return PolymorphApi.common().getRecipeData(pTileEntity)
        .map(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, new ArrayList<>()))
        .orElse(Optional.empty());
  }
}
