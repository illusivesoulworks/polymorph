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

package top.theillusivec4.polymorph.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketStackRecipeSelection;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketPlayerRecipeSync;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipesList;

public class PolymorphNetwork {

  private static final String PTC_VERSION = "1";

  private static SimpleChannel instance;
  private static int id = 0;

  public static SimpleChannel get() {
    return instance;
  }

  public static void setup() {
    instance =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(PolymorphApi.MOD_ID, "main"))
            .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
            .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Client-to-Server
    register(CPacketPlayerRecipeSelection.class, CPacketPlayerRecipeSelection::encode,
        CPacketPlayerRecipeSelection::decode, CPacketPlayerRecipeSelection::handle);
    register(CPacketPersistentRecipeSelection.class, CPacketPersistentRecipeSelection::encode,
        CPacketPersistentRecipeSelection::decode, CPacketPersistentRecipeSelection::handle);
    register(CPacketStackRecipeSelection.class, CPacketStackRecipeSelection::encode,
        CPacketStackRecipeSelection::decode, CPacketStackRecipeSelection::handle);

    // Server-to-Client
    register(SPacketRecipesList.class, SPacketRecipesList::encode, SPacketRecipesList::decode,
        SPacketRecipesList::handle);
    register(SPacketHighlightRecipe.class, SPacketHighlightRecipe::encode,
        SPacketHighlightRecipe::decode, SPacketHighlightRecipe::handle);
    register(SPacketPlayerRecipeSync.class, SPacketPlayerRecipeSync::encode,
        SPacketPlayerRecipeSync::decode, SPacketPlayerRecipeSync::handle);
  }

  public static <M> void register(Class<M> pClass, BiConsumer<M, PacketBuffer> pEncoder,
                                  Function<PacketBuffer, M> pDecoder,
                                  BiConsumer<M, Supplier<NetworkEvent.Context>> pMessage) {
    instance.registerMessage(id++, pClass, pEncoder, pDecoder, pMessage);
  }
}
