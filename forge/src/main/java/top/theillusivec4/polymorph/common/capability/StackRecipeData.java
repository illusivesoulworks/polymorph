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

package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public class StackRecipeData extends AbstractRecipeData<ItemStack> implements IStackRecipeData {

  public StackRecipeData(ItemStack pOwner) {
    super(pOwner);
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    Set<ServerPlayerEntity> players = new HashSet<>();
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getServer().ifPresent(server -> {
      for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
        commonApi.getRecipeDataFromItemStack(player.openContainer)
            .ifPresent(recipeData -> {
              if (recipeData == this) {
                players.add(player);
              }
            });
      }
    });
    return players;
  }
}
