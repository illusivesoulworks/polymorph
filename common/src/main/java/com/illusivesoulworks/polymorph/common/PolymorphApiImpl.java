/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common;

import com.google.common.collect.ImmutableMap;
import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphNetwork;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphRecipeManager;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import com.illusivesoulworks.polymorph.common.capability.PolymorphCapabilities;
import com.illusivesoulworks.polymorph.platform.Services;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PolymorphApiImpl extends PolymorphApi {

  public static final PolymorphApi INSTANCE = new PolymorphApiImpl();

  private final List<IBlockEntityFactory> blockEntityFactories = new CopyOnWriteArrayList<>();
  private final List<IRecipeDataFactory> recipeDataFactories = new CopyOnWriteArrayList<>();
  private final Map<Class<? extends BlockEntity>, IRecipeDataFactory>
      blockEntity2RecipeDataFactory = new ConcurrentHashMap<>();
  private final IPolymorphNetwork distributor = Services.PLATFORM.getPacketDistributor();
  private final IPolymorphRecipeManager recipeManager = new PolymorphRecipeManager();

  @Override
  public IPolymorphNetwork getNetwork() {
    return this.distributor;
  }

  @Override
  public IPolymorphRecipeManager getRecipeManager() {
    return recipeManager;
  }

  @Override
  public IBlockEntityRecipeData createBlockEntityRecipeData(BlockEntity blockEntity) {

    for (IRecipeDataFactory function : this.recipeDataFactories) {
      IBlockEntityRecipeData recipeData = function.createRecipeData(blockEntity);

      if (recipeData != null) {
        return recipeData;
      }
    }
    return null;
  }

  @Override
  public IBlockEntityRecipeData getBlockEntityRecipeData(BlockEntity blockEntity) {
    return PolymorphCapabilities.getRecipeData(blockEntity);
  }

  @Override
  public IBlockEntityRecipeData getBlockEntityRecipeData(AbstractContainerMenu container) {

    for (IBlockEntityFactory function : this.blockEntityFactories) {
      BlockEntity blockEntity = function.getBlockEntity(container);

      if (blockEntity != null) {
        return this.getBlockEntityRecipeData(blockEntity);
      }
    }
    return null;
  }

  @Override
  public IPlayerRecipeData getPlayerRecipeData(Player player) {
    return PolymorphCapabilities.getRecipeData(player);
  }

  @Override
  public Map<Class<? extends BlockEntity>, IRecipeDataFactory> getBlockEntities() {
    return ImmutableMap.copyOf(this.blockEntity2RecipeDataFactory);
  }

  @Override
  public void registerMenu(IBlockEntityFactory blockEntityFactory) {

    if (blockEntityFactory == null) {
      PolymorphConstants.LOG.error("Attempted to register a null IBlockEntityFactory");
      return;
    }
    this.blockEntityFactories.add(blockEntityFactory);
  }

  @Override
  public void registerBlockEntity(IRecipeDataFactory recipeDataFactory) {

    if (recipeDataFactory == null) {
      PolymorphConstants.LOG.error("Attempted to register a null IRecipeDataFactory");
      return;
    }
    this.recipeDataFactories.add(recipeDataFactory);
  }

  @Override
  public void registerBlockEntity(Class<? extends BlockEntity> blockEntity,
                                  IRecipeDataFactory recipeDataFactory) {

    if (recipeDataFactory == null) {
      PolymorphConstants.LOG.error("Attempted to register a null IRecipeDataFactory");
      return;
    }
    this.blockEntity2RecipeDataFactory.put(blockEntity, recipeDataFactory);
    this.recipeDataFactories.add(recipeDataFactory);
  }
}
