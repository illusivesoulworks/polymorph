/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.impl;

import io.netty.buffer.Unpooled;
import java.util.List;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import top.theillusivec4.polymorph.core.base.common.PacketVendor;
import top.theillusivec4.polymorph.loader.network.NetworkPackets;

public class PacketVendorImpl implements PacketVendor {

  @Override
  public void sendSetRecipe(String recipeId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(recipeId);
    ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkPackets.SET_RECIPE, buf);
  }

  @Override
  public void sendTransferRecipe(String recipeId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(recipeId);
    ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkPackets.TRANSFER_RECIPE, buf);
  }

  @Override
  public void sendRecipes(List<String> recipes, PlayerEntity player) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    for (String recipe : recipes) {
      buf.writeString(recipe);
    }
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkPackets.SEND_RECIPES, buf);
  }

  @Override
  public void fetchRecipes() {
    ClientSidePacketRegistry.INSTANCE
        .sendToServer(NetworkPackets.FETCH_RECIPES, new PacketByteBuf(Unpooled.buffer()));
  }
}
