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

import com.illusivesoulworks.polymorph.client.ClientEventsListener;
import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.network.server.SPacketUpdatePreview;
import java.util.function.Consumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PolymorphFabricClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    PolymorphCommonMod.clientSetup();
    ClientEventsListener.setup();

    registerClientReceiver(SPacketHighlightRecipe.TYPE, SPacketHighlightRecipe::handle);
    registerClientReceiver(SPacketPlayerRecipeSync.TYPE, SPacketPlayerRecipeSync::handle);
    registerClientReceiver(SPacketRecipesList.TYPE, SPacketRecipesList::handle);
    registerClientReceiver(SPacketUpdatePreview.TYPE, SPacketUpdatePreview::handle);
  }

  private static <M extends CustomPacketPayload> void registerClientReceiver(
      CustomPacketPayload.Type<M> type, Consumer<M> handler) {
    ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
      Minecraft mc = context.client();

      if (mc != null) {
        mc.execute(() -> handler.accept(payload));
      }
    });
  }
}
