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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public class RecipeSelection {

  public static <T extends Recipe<C>, C extends Container> Optional<T> getPlayerRecipe(
      AbstractContainerMenu containerMenu, RecipeType<T> pType, C pInventory, Level pWorld,
      List<Slot> slots) {
    Player player = null;

    for (Slot slot : slots) {

      if (slot.container instanceof Inventory inv) {
        player = inv.player;
        break;
      }
    }

    if (player != null) {
      return getPlayerRecipe(containerMenu, pType, pInventory, pWorld, player, new ArrayList<>());
    } else {
      return pWorld.getRecipeManager().getRecipesFor(pType, pInventory, pWorld).stream()
          .findFirst();
    }
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getPlayerRecipe(
      AbstractContainerMenu containerMenu, RecipeType<T> pType, C pInventory, Level pWorld,
      Player pPlayer) {
    return getPlayerRecipe(containerMenu, pType, pInventory, pWorld, pPlayer, new ArrayList<>());
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getPlayerRecipe(
      AbstractContainerMenu containerMenu, RecipeType<T> pType, C pInventory, Level pWorld,
      Player pPlayer, List<T> pRecipes) {
    LazyOptional<IPlayerRecipeData> maybeData = PolymorphApi.common().getRecipeData(pPlayer);
    maybeData.ifPresent(recipeData -> recipeData.setContainerMenu(containerMenu));
    return getRecipe(pType, pInventory, pWorld, maybeData, pRecipes);
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getStackRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, ItemStack pStack) {
    LazyOptional<IStackRecipeData> maybeData = PolymorphApi.common().getRecipeData(pStack);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getTileEntityRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, BlockEntity pTileEntity) {
    LazyOptional<IBlockEntityRecipeData> maybeData =
        PolymorphApi.common().getRecipeData(pTileEntity);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  private static <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(
      RecipeType<T> pType, C pInventory, Level pWorld, LazyOptional<? extends IRecipeData<?>> pOpt,
      List<T> pRecipes) {

    if (pOpt.isPresent()) {
      return pOpt.map(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, pRecipes))
          .orElse(Optional.empty());
    } else {
      return pWorld.getRecipeManager().getRecipesFor(pType, pInventory, pWorld).stream()
          .findFirst();
    }
  }
}
