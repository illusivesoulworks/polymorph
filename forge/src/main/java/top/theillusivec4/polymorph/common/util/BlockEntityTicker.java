/*
 * Copyright (C) 2020-2022 C4
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

package top.theillusivec4.polymorph.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public class BlockEntityTicker {

  private static final Map<TileEntity, ITileEntityRecipeData> TICKABLE_BLOCKS =
      new HashMap<>();

  public static void tick() {
    List<TileEntity> toRemove = new ArrayList<>();

    for (Map.Entry<TileEntity, ITileEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      TileEntity be = entry.getKey();

      if (be.isRemoved() || (be.getWorld() != null && be.getWorld().isRemote())) {
        toRemove.add(be);
      } else {
        entry.getValue().tick();
      }
    }

    for (TileEntity be : toRemove) {
      TICKABLE_BLOCKS.remove(be);
    }
  }

  public static void add(ServerPlayerEntity serverPlayer, ITileEntityRecipeData recipeData) {
    ITileEntityRecipeData data = TICKABLE_BLOCKS.get(recipeData.getOwner());

    if (data != null) {
      data.addListener(serverPlayer);
    } else {
      recipeData.addListener(serverPlayer);
      TICKABLE_BLOCKS.put(recipeData.getOwner(), recipeData);
    }
  }

  public static void remove(ServerPlayerEntity serverPlayer) {
    List<TileEntity> toRemove = new ArrayList<>();

    for (Map.Entry<TileEntity, ITileEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      entry.getValue().removeListener(serverPlayer);

      if (entry.getValue().getListeners().isEmpty()) {
        toRemove.add(entry.getKey());
      }
    }

    for (TileEntity blockEntity : toRemove) {
      TICKABLE_BLOCKS.remove(blockEntity);
    }
  }
}
