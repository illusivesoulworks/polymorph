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

package top.theillusivec4.polymorph.common.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;

public class PolymorphCommon implements IPolymorphCommon {

  private static final IPolymorphCommon INSTANCE = new PolymorphCommon();

  public static IPolymorphCommon get() {
    return INSTANCE;
  }

  private final List<ITileEntity2RecipeData> tileEntity2RecipeData = new LinkedList<>();
  private final List<IContainer2TileEntity> container2TileEntities = new LinkedList<>();
  private final List<IContainer2ItemStack> container2ItemStacks = new LinkedList<>();
  private final List<IItemStack2RecipeData> itemStack2RecipeData = new LinkedList<>();
  private final IPolymorphPacketDistributor distributor = new PolymorphPacketDistributor();

  private MinecraftServer server = null;

  @Override
  public IPolymorphPacketDistributor getPacketDistributor() {
    return distributor;
  }

  @Override
  public void setServer(MinecraftServer pServer) {
    this.server = pServer;
  }

  @Override
  public Optional<MinecraftServer> getServer() {
    return Optional.ofNullable(this.server);
  }

  @Override
  public Optional<IBlockEntityRecipeData> tryCreateRecipeData(BlockEntity pTileEntity) {

    for (ITileEntity2RecipeData function : this.tileEntity2RecipeData) {
      IBlockEntityRecipeData recipeData = function.createRecipeData(pTileEntity);

      if (recipeData != null) {
        return Optional.of(recipeData);
      }
    }
    return Optional.empty();
  }

  @Override
  public LazyOptional<IBlockEntityRecipeData> getRecipeData(BlockEntity pTileEntity) {
    return PolymorphCapabilities.getRecipeData(pTileEntity);
  }

  @Override
  public LazyOptional<IBlockEntityRecipeData> getRecipeDataFromTileEntity(
      AbstractContainerMenu pContainer) {

    for (IContainer2TileEntity function : this.container2TileEntities) {
      BlockEntity tileEntity = function.getTileEntity(pContainer);

      if (tileEntity != null) {
        return this.getRecipeData(tileEntity);
      }
    }
    return LazyOptional.empty();
  }

  @Override
  public Optional<IStackRecipeData> tryCreateRecipeData(ItemStack pStack) {

    for (IItemStack2RecipeData function : this.itemStack2RecipeData) {
      IStackRecipeData recipeData = function.createRecipeData(pStack);

      if (recipeData != null) {
        return Optional.of(recipeData);
      }
    }
    return Optional.empty();
  }

  @Override
  public LazyOptional<IStackRecipeData> getRecipeData(ItemStack pStack) {
    return PolymorphCapabilities.getRecipeData(pStack);
  }

  @Override
  public LazyOptional<IStackRecipeData> getRecipeDataFromItemStack(
      AbstractContainerMenu pContainer) {

    for (IContainer2ItemStack function : this.container2ItemStacks) {
      ItemStack itemstack = function.getItemStack(pContainer);

      if (!itemstack.isEmpty()) {
        return this.getRecipeData(itemstack);
      }
    }
    return LazyOptional.empty();
  }

  @Override
  public LazyOptional<IPlayerRecipeData> getRecipeData(Player pPlayer) {
    return PolymorphCapabilities.getRecipeData(pPlayer);
  }

  @Override
  public void registerTileEntity2RecipeData(
      ITileEntity2RecipeData pTileEntity2PersistentDataset) {
    this.tileEntity2RecipeData.add(pTileEntity2PersistentDataset);
  }

  @Override
  public void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity) {
    this.container2TileEntities.add(pContainer2TileEntity);
  }

  @Override
  public void registerItemStack2RecipeData(IItemStack2RecipeData pItemStack2RecipeData) {
    this.itemStack2RecipeData.add(pItemStack2RecipeData);
  }

  @Override
  public void registerContainer2ItemStack(IContainer2ItemStack pContainer2ItemStack) {
    this.container2ItemStacks.add(pContainer2ItemStack);
  }
}
