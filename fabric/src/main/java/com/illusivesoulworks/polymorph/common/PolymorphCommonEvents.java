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

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphCommon;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphPacketDistributor;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import com.illusivesoulworks.polymorph.common.util.BlockEntityTicker;
import com.mojang.datafixers.util.Pair;
import java.util.SortedSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class PolymorphCommonEvents {

  public static void levelTick(Level level) {

    if (!level.isClientSide() && level.getGameTime() % 5 == 0) {
      BlockEntityTicker.tick();
    }
  }

  public static void playerDisconnected(ServerPlayer serverPlayer) {
    BlockEntityTicker.remove(serverPlayer);
  }

  public static void openContainer(Player player, AbstractContainerMenu containerMenu) {

    if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayerEntity) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeDataFromBlockEntity(containerMenu).ifPresent(
          recipeData -> {
            IPolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();

            if (recipeData.isFailing() || recipeData.isEmpty(null)) {
              packetDistributor.sendRecipesListS2C(serverPlayerEntity);
            } else {
              Pair<SortedSet<IRecipePair>, ResourceLocation> data = recipeData.getPacketData();
              packetDistributor.sendRecipesListS2C(serverPlayerEntity, data.getFirst(),
                  data.getSecond());
            }
          });
      PolymorphIntegrations.openContainer(containerMenu, serverPlayerEntity);
    }
  }
}
