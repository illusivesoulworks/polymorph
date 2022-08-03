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
import com.illusivesoulworks.polymorph.common.network.client.CPacketBlockEntityListener;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketStackRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.server.SPacketBlockEntityRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PolymorphFabricNetwork {

  public static final ResourceLocation PLAYER_SELECT =
      new ResourceLocation(PolymorphApi.MOD_ID, "player_select");
  public static final ResourceLocation PERSISTENT_SELECT =
      new ResourceLocation(PolymorphApi.MOD_ID, "persistent_select");
  public static final ResourceLocation STACK_SELECT =
      new ResourceLocation(PolymorphApi.MOD_ID, "stack_select");
  public static final ResourceLocation RECIPES_LIST =
      new ResourceLocation(PolymorphApi.MOD_ID, "recipes_list");
  public static final ResourceLocation HIGHLIGHT_RECIPE =
      new ResourceLocation(PolymorphApi.MOD_ID, "highlight_recipe");
  public static final ResourceLocation RECIPE_SYNC =
      new ResourceLocation(PolymorphApi.MOD_ID, "recipe_sync");
  public static final ResourceLocation BLOCK_ENTITY_SYNC =
      new ResourceLocation(PolymorphApi.MOD_ID, "block_entity_sync");
  public static final ResourceLocation BLOCK_ENTITY_LISTENER =
      new ResourceLocation(PolymorphApi.MOD_ID, "block_entity_listener");

  public static void setup() {
    registerServerReceiver(PLAYER_SELECT, CPacketPlayerRecipeSelection::decode,
        CPacketPlayerRecipeSelection::handle);
    registerServerReceiver(PERSISTENT_SELECT, CPacketPersistentRecipeSelection::decode,
        CPacketPersistentRecipeSelection::handle);
    registerServerReceiver(STACK_SELECT, CPacketStackRecipeSelection::decode,
        CPacketStackRecipeSelection::handle);
    registerServerReceiver(BLOCK_ENTITY_LISTENER, CPacketBlockEntityListener::decode,
        CPacketBlockEntityListener::handle);
  }

  public static void clientSetup() {
    registerClientReceiver(HIGHLIGHT_RECIPE, SPacketHighlightRecipe::decode,
        SPacketHighlightRecipe::handle);
    registerClientReceiver(RECIPE_SYNC, SPacketPlayerRecipeSync::decode,
        SPacketPlayerRecipeSync::handle);
    registerClientReceiver(RECIPES_LIST, SPacketRecipesList::decode, SPacketRecipesList::handle);
    registerClientReceiver(BLOCK_ENTITY_SYNC, SPacketBlockEntityRecipeSync::decode,
        SPacketBlockEntityRecipeSync::handle);
  }

  private static <M> void registerServerReceiver(ResourceLocation resourceLocation,
                                                 Function<FriendlyByteBuf, M> decoder,
                                                 BiConsumer<M, ServerPlayer> handler) {
    ServerPlayNetworking.registerGlobalReceiver(resourceLocation,
        (server, player, listener, buf, responseSender) -> {
          M packet = decoder.apply(buf);
          server.execute(() -> handler.accept(packet, player));
        });
  }

  private static <M> void registerClientReceiver(ResourceLocation resourceLocation,
                                                 Function<FriendlyByteBuf, M> decoder,
                                                 Consumer<M> handler) {
    ClientPlayNetworking.registerGlobalReceiver(resourceLocation,
        (client, listener, buf, responseSender) -> {
          M packet = decoder.apply(buf);
          client.execute(() -> handler.accept(packet));
        });
  }
}
