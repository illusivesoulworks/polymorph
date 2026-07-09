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

package com.illusivesoulworks.polymorph.common.capability;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphCommon;
import com.illusivesoulworks.polymorph.api.common.capability.IStackRecipeData;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class StackRecipeData extends AbstractRecipeData<ItemStack> implements IStackRecipeData {

  public StackRecipeData(ItemStack owner) {
    super(owner);
  }

  @Override
  public Set<ServerPlayer> getListeners() {
    Set<ServerPlayer> players = new HashSet<>();
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getServer().ifPresent(server -> {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        commonApi.getRecipeDataFromItemStack(player.containerMenu)
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
