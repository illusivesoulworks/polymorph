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
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphPacketDistributor;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.network.client.CPacketBlockEntityListener;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketStackRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.server.SPacketBlockEntityRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import java.util.SortedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class PolymorphQuiltPacketDistributor implements IPolymorphPacketDistributor {

  @Override
  public void sendPlayerRecipeSelectionC2S(ResourceLocation resourceLocation) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    CPacketPlayerRecipeSelection.encode(new CPacketPlayerRecipeSelection(resourceLocation), buf);
    ClientPlayNetworking.send(PolymorphQuiltNetwork.PLAYER_SELECT, buf);
  }

  @Override
  public void sendPersistentRecipeSelectionC2S(ResourceLocation resourceLocation) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    CPacketPersistentRecipeSelection.encode(new CPacketPersistentRecipeSelection(resourceLocation),
        buf);
    ClientPlayNetworking.send(PolymorphQuiltNetwork.PERSISTENT_SELECT, buf);
  }

  @Override
  public void sendStackRecipeSelectionC2S(ResourceLocation resourceLocation) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    CPacketStackRecipeSelection.encode(new CPacketStackRecipeSelection(resourceLocation), buf);
    ClientPlayNetworking.send(PolymorphQuiltNetwork.STACK_SELECT, buf);
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
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketRecipesList.encode(new SPacketRecipesList(recipesList, selected), buf);
    ServerPlayNetworking.send(player, PolymorphQuiltNetwork.RECIPES_LIST, buf);
  }

  @Override
  public void sendHighlightRecipeS2C(ServerPlayer player, ResourceLocation resourceLocation) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketHighlightRecipe.encode(new SPacketHighlightRecipe(resourceLocation), buf);
    ServerPlayNetworking.send(player, PolymorphQuiltNetwork.HIGHLIGHT_RECIPE, buf);
  }

  @Override
  public void sendPlayerSyncS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList,
                                ResourceLocation selected) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketPlayerRecipeSync.encode(new SPacketPlayerRecipeSync(recipesList, selected), buf);
    ServerPlayNetworking.send(player, PolymorphQuiltNetwork.RECIPE_SYNC, buf);
  }

  @Override
  public void sendBlockEntitySyncS2C(BlockPos blockPos, ResourceLocation selected) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketBlockEntityRecipeSync.encode(new SPacketBlockEntityRecipeSync(blockPos, selected), buf);
    PolymorphApi.common().getServer().ifPresent(server -> PlayerLookup.all(server).forEach(
        player -> ServerPlayNetworking.send(player, PolymorphQuiltNetwork.BLOCK_ENTITY_SYNC,
            buf)));
  }

  @Override
  public void sendBlockEntityListenerC2S(boolean add) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    CPacketBlockEntityListener.encode(new CPacketBlockEntityListener(add), buf);
    ClientPlayNetworking.send(PolymorphQuiltNetwork.BLOCK_ENTITY_LISTENER, buf);
  }
}
