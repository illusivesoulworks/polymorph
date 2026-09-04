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

import com.illusivesoulworks.polymorph.api.common.base.IPolymorphNetwork;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.network.client.CPacketBlockEntityListener;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.network.server.SPacketUpdatePreview;
import java.util.HashSet;
import java.util.Optional;
import java.util.SortedSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PolymorphForgeNetwork implements IPolymorphNetwork {

  @Override
  public void sendPlayerRecipeSelectionC2S(ResourceLocation resourceLocation) {
    PolymorphForgeChannel.get()
        .send(new CPacketPlayerRecipeSelection(resourceLocation), PacketDistributor.SERVER.noArg());
  }

  @Override
  public void sendPersistentRecipeSelectionC2S(ResourceLocation resourceLocation) {
    PolymorphForgeChannel.get()
        .send(new CPacketPersistentRecipeSelection(resourceLocation),
            PacketDistributor.SERVER.noArg());
  }

  @Override
  public void sendRecipesListS2C(ServerPlayer player) {
    sendRecipesListS2C(player, null);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList) {
    sendRecipesListS2C(player, recipesList, null);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList,
                                 ResourceLocation selected) {
    HashSet<IRecipePair> set = null;

    if (recipesList != null) {
      set = new HashSet<>(recipesList);
    }
    PolymorphForgeChannel.get()
        .send(new SPacketRecipesList(Optional.ofNullable(set), Optional.ofNullable(selected)),
            PacketDistributor.PLAYER.with(player));
  }

  @Override
  public void sendPlayerSyncS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList,
                                ResourceLocation selected) {
    HashSet<IRecipePair> set = null;

    if (recipesList != null) {
      set = new HashSet<>(recipesList);
    }
    PolymorphForgeChannel.get()
        .send(new SPacketPlayerRecipeSync(Optional.ofNullable(set), Optional.ofNullable(selected)),
            PacketDistributor.PLAYER.with(player));
  }

  @Override
  public void sendBlockEntityListenerC2S(boolean add) {
    PolymorphForgeChannel.get()
        .send(new CPacketBlockEntityListener(add), PacketDistributor.SERVER.noArg());
  }

  @Override
  public void sendUpdatePreviewS2C(ServerPlayer player) {
    PolymorphForgeChannel.get()
        .send(SPacketUpdatePreview.INSTANCE, PacketDistributor.PLAYER.with(player));
  }
}
