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

package top.theillusivec4.polymorph.api.common.base;

import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public interface IPolymorphCommon {

  Optional<IBlockEntityRecipeData> tryCreateRecipeData(BlockEntity pTileEntity);

  LazyOptional<IBlockEntityRecipeData> getRecipeData(BlockEntity pTileEntity);

  LazyOptional<IBlockEntityRecipeData> getRecipeDataFromTileEntity(AbstractContainerMenu pContainer);

  Optional<IStackRecipeData> tryCreateRecipeData(ItemStack pStack);

  LazyOptional<IStackRecipeData> getRecipeData(ItemStack pStack);

  LazyOptional<IStackRecipeData> getRecipeDataFromItemStack(AbstractContainerMenu container);

  LazyOptional<IPlayerRecipeData> getRecipeData(Player pPlayer);

  void registerTileEntity2RecipeData(ITileEntity2RecipeData pTileEntity2RecipeData);

  void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity);

  void registerItemStack2RecipeData(IItemStack2RecipeData pItemStack2RecipeData);

  void registerContainer2ItemStack(IContainer2ItemStack pContainer2ItemStack);

  IPolymorphPacketDistributor getPacketDistributor();

  void setServer(MinecraftServer pServer);

  Optional<MinecraftServer> getServer();

  interface IItemStack2RecipeData {

    IStackRecipeData createRecipeData(ItemStack pStack);
  }

  interface ITileEntity2RecipeData {

    IBlockEntityRecipeData createRecipeData(BlockEntity pTileEntity);
  }

  interface IContainer2TileEntity {

    BlockEntity getTileEntity(AbstractContainerMenu pContainer);
  }

  interface IContainer2ItemStack {

    ItemStack getItemStack(AbstractContainerMenu pContainer);
  }
}
