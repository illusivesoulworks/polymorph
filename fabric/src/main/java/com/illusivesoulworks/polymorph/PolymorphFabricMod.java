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

package com.illusivesoulworks.polymorph;

import com.illusivesoulworks.polymorph.common.CommonEventsListener;
import com.illusivesoulworks.polymorph.common.components.PolymorphFabricComponents;
import com.illusivesoulworks.polymorph.common.network.client.CPacketBlockEntityListener;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.network.server.SPacketUpdatePreview;
import com.illusivesoulworks.polymorph.server.PolymorphCommands;
import java.util.function.BiConsumer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PolymorphFabricMod implements ModInitializer {

  @Override
  public void onInitialize() {
    PolymorphCommonMod.init();
    PolymorphCommonMod.setup();
    registerPayloads();
    PolymorphFabricComponents.setup();
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> PolymorphCommands.register(dispatcher));
    CommonEventsListener.setup();
  }

  public static void registerPayloads() {
    PayloadTypeRegistry.playC2S()
        .register(CPacketPlayerRecipeSelection.TYPE, CPacketPlayerRecipeSelection.STREAM_CODEC);
    PayloadTypeRegistry.playC2S().register(CPacketPersistentRecipeSelection.TYPE,
        CPacketPersistentRecipeSelection.STREAM_CODEC);
    PayloadTypeRegistry.playC2S()
        .register(CPacketBlockEntityListener.TYPE, CPacketBlockEntityListener.STREAM_CODEC);
    PayloadTypeRegistry.playS2C()
        .register(SPacketHighlightRecipe.TYPE, SPacketHighlightRecipe.STREAM_CODEC);
    PayloadTypeRegistry.playS2C()
        .register(SPacketPlayerRecipeSync.TYPE, SPacketPlayerRecipeSync.STREAM_CODEC);
    PayloadTypeRegistry.playS2C()
        .register(SPacketRecipesList.TYPE, SPacketRecipesList.STREAM_CODEC);
    PayloadTypeRegistry.playS2C()
        .register(SPacketUpdatePreview.TYPE, SPacketUpdatePreview.STREAM_CODEC);

    registerServerReceiver(CPacketPlayerRecipeSelection.TYPE, CPacketPlayerRecipeSelection::handle);
    registerServerReceiver(CPacketPersistentRecipeSelection.TYPE,
        CPacketPersistentRecipeSelection::handle);
    registerServerReceiver(CPacketBlockEntityListener.TYPE, CPacketBlockEntityListener::handle);
  }

  private static <M extends CustomPacketPayload> void registerServerReceiver(
      CustomPacketPayload.Type<M> type,
      BiConsumer<M, ServerPlayer> handler) {
    ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
      ServerPlayer serverPlayer = context.player();
      MinecraftServer server = serverPlayer.getServer();

      if (server != null) {
        server.execute(() -> handler.accept(payload, serverPlayer));
      }
    });
  }
}
