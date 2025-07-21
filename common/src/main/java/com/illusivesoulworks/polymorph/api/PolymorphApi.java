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

package com.illusivesoulworks.polymorph.api;

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphNetwork;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphRecipeManager;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class PolymorphApi {

  public static final String MOD_ID = PolymorphConstants.MOD_ID;

  private static PolymorphApi instance;

  public static PolymorphApi getInstance() {

    if (instance == null) {
      throw new RuntimeException("Missing Polymorph API implementation!");
    }
    return instance;
  }

  public abstract IPolymorphNetwork getNetwork();

  public abstract IPolymorphRecipeManager getRecipeManager();

  public abstract IBlockEntityRecipeData createBlockEntityRecipeData(BlockEntity blockEntity);

  public abstract IBlockEntityRecipeData getBlockEntityRecipeData(BlockEntity blockEntity);

  public abstract IBlockEntityRecipeData getBlockEntityRecipeData(
      AbstractContainerMenu containerMenu);

  public abstract IPlayerRecipeData getPlayerRecipeData(Player player);

  public abstract Map<Class<? extends BlockEntity>, IRecipeDataFactory> getBlockEntities();

  /**
   * @see #registerBlockEntity(Class, IRecipeDataFactory)
   * @deprecated
   */
  @Deprecated
  public abstract void registerBlockEntity(IRecipeDataFactory blockEntity2RecipeData);

  public abstract void registerBlockEntity(Class<? extends BlockEntity> blockEntity,
                                           IRecipeDataFactory blockEntity2RecipeData);

  public abstract void registerMenu(IBlockEntityFactory container2BlockEntity);

  public interface IRecipeDataFactory {

    IBlockEntityRecipeData createRecipeData(BlockEntity blockEntity);
  }

  public interface IBlockEntityFactory {

    BlockEntity getBlockEntity(AbstractContainerMenu containerMenu);
  }
}
