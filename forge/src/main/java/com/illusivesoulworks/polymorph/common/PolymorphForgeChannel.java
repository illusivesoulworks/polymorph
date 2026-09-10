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
import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.network.server.SPacketUpdatePreview;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class PolymorphForgeChannel {

  private static final int PTC_VERSION = 1;

  private static SimpleChannel instance;

  public static SimpleChannel get() {
    return instance;
  }

  public static void setup() {
    instance = ChannelBuilder
        .named(ResourceLocation.fromNamespaceAndPath(PolymorphApi.MOD_ID, "main"))
        .networkProtocolVersion(PTC_VERSION)
        .clientAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION))
        .serverAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION))
        .simpleChannel();

    // Server-to-Client
    registerS2CPlay(SPacketRecipesList.class, SPacketRecipesList.STREAM_CODEC::encode,
        SPacketRecipesList.STREAM_CODEC::decode, SPacketRecipesList::handle);
    registerS2C(SPacketHighlightRecipe.class, SPacketHighlightRecipe.STREAM_CODEC::encode,
        SPacketHighlightRecipe.STREAM_CODEC::decode, SPacketHighlightRecipe::handle);
    registerS2CPlay(SPacketPlayerRecipeSync.class, SPacketPlayerRecipeSync.STREAM_CODEC::encode,
        SPacketPlayerRecipeSync.STREAM_CODEC::decode, SPacketPlayerRecipeSync::handle);
    registerS2CPlay(SPacketUpdatePreview.class, SPacketUpdatePreview.STREAM_CODEC::encode,
        SPacketUpdatePreview.STREAM_CODEC::decode, SPacketUpdatePreview::handle);

    // Client-to-Server
    registerC2S(CPacketPlayerRecipeSelection.class,
        CPacketPlayerRecipeSelection.STREAM_CODEC::encode,
        CPacketPlayerRecipeSelection.STREAM_CODEC::decode, CPacketPlayerRecipeSelection::handle);
    registerC2S(CPacketPersistentRecipeSelection.class,
        CPacketPersistentRecipeSelection.STREAM_CODEC::encode,
        CPacketPersistentRecipeSelection.STREAM_CODEC::decode,
        CPacketPersistentRecipeSelection::handle);
    registerC2S(CPacketBlockEntityListener.class, CPacketBlockEntityListener.STREAM_CODEC::encode,
        CPacketBlockEntityListener.STREAM_CODEC::decode, CPacketBlockEntityListener::handle);
  }

  public static <M> void registerC2S(Class<M> clazz, BiConsumer<FriendlyByteBuf, M> encoder,
                                     Function<FriendlyByteBuf, M> decoder,
                                     BiConsumer<M, ServerPlayer> handler) {
    instance.messageBuilder(clazz)
        .encoder((m, buf) -> encoder.accept(buf, m))
        .decoder(decoder)
        .consumerNetworkThread((m, context) -> {
          context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player != null) {
              handler.accept(m, player);
            }
          });
          context.setPacketHandled(true);
        })
        .add();
  }

  public static <M extends CustomPacketPayload> void registerS2C(Class<M> clazz,
                                                                 BiConsumer<FriendlyByteBuf, M> encoder,
                                                                 Function<FriendlyByteBuf, M> decoder,
                                                                 Consumer<M> handler) {
    instance.messageBuilder(clazz)
        .encoder((m, buf) -> encoder.accept(buf, m))
        .decoder(decoder)
        .consumerNetworkThread((BiConsumer<M, CustomPayloadEvent.Context>) (m, context) ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                  context.enqueueWork(() -> handler.accept(m));
                  context.setPacketHandled(true);
                }))
        .add();
  }

  public static <M extends CustomPacketPayload> void registerS2CPlay(Class<M> clazz,
                                                                     BiConsumer<RegistryFriendlyByteBuf, M> encoder,
                                                                     Function<RegistryFriendlyByteBuf, M> decoder,
                                                                     Consumer<M> handler) {
    instance.messageBuilder(clazz)
        .encoder((m, buf) -> encoder.accept((RegistryFriendlyByteBuf) buf, m))
        .decoder(buf -> decoder.apply((RegistryFriendlyByteBuf) buf))
        .consumerNetworkThread((BiConsumer<M, CustomPayloadEvent.Context>) (m, context) ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                  context.enqueueWork(() -> handler.accept(m));
                  context.setPacketHandled(true);
                }))
        .add();
  }
}
