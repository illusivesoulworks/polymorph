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

package com.illusivesoulworks.polymorph.common.util;

import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityTicker {

  private static final Map<BlockEntity, IBlockEntityRecipeData> TICKABLE_BLOCKS =
      new ConcurrentHashMap<>();

  public static void tick() {
    List<BlockEntity> toRemove = new ArrayList<>();

    for (Map.Entry<BlockEntity, IBlockEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      BlockEntity be = entry.getKey();

      if (be.isRemoved() || be.getLevel() == null || be.getLevel().isClientSide()) {
        toRemove.add(be);
      } else {
        entry.getValue().tick();
      }
    }

    for (BlockEntity be : toRemove) {
      TICKABLE_BLOCKS.remove(be);
    }
  }

  public static void add(ServerPlayer serverPlayer, IBlockEntityRecipeData recipeData) {
    IBlockEntityRecipeData data = TICKABLE_BLOCKS.get(recipeData.getOwner());

    if (data != null) {
      data.addListener(serverPlayer);
    } else {
      recipeData.addListener(serverPlayer);
      TICKABLE_BLOCKS.put(recipeData.getOwner(), recipeData);
    }
  }

  public static void remove(ServerPlayer serverPlayer) {
    List<BlockEntity> toRemove = new ArrayList<>();

    for (Map.Entry<BlockEntity, IBlockEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      entry.getValue().removeListener(serverPlayer);

      if (entry.getValue().getListeners().isEmpty()) {
        toRemove.add(entry.getKey());
      }
    }

    for (BlockEntity blockEntity : toRemove) {
      TICKABLE_BLOCKS.remove(blockEntity);
    }
  }

  public static void clear() {
    TICKABLE_BLOCKS.clear();
  }
}
