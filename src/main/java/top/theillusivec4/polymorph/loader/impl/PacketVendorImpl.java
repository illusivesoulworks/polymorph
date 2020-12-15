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

import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import top.theillusivec4.polymorph.core.base.common.PacketVendor;
import top.theillusivec4.polymorph.loader.network.NetworkPackets;

public class PacketVendorImpl implements PacketVendor {

  @Override
  public void sendSetRecipe(String recipeId) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeString(recipeId);
    ClientPlayNetworking.send(NetworkPackets.SET_RECIPE, buf);
  }

  @Override
  public void sendSetCraftingRecipe(String recipeId) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeString(recipeId);
    ClientPlayNetworking.send(NetworkPackets.SET_RECIPE, buf);
  }

  @Override
  public void sendTransferRecipe(String recipeId) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeString(recipeId);
    ClientPlayNetworking.send(NetworkPackets.TRANSFER_RECIPE, buf);
  }

  @Override
  public void sendRecipes(List<String> recipes, String selectedRecipe, ServerPlayerEntity player) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeInt(recipes.size());

    for (String id : recipes) {
      buf.writeString(id);
    }
    buf.writeString(selectedRecipe);
    ServerPlayNetworking.send(player, NetworkPackets.SEND_RECIPES, buf);
  }

  @Override
  public void syncOutput(ItemStack stack, ServerPlayerEntity player) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeItemStack(ItemStack.EMPTY);
    ServerPlayNetworking.send(player, NetworkPackets.SYNC_OUTPUT, buf);
  }

  @Override
  public void highlightRecipe(String recipeId, ServerPlayerEntity player) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeString(recipeId);
    ServerPlayNetworking.send(player, NetworkPackets.HIGHLIGHT_RECIPE, buf);
  }

  @Override
  public void fetchRecipes() {
    ClientPlayNetworking.send(NetworkPackets.FETCH_RECIPES, PacketByteBufs.create());
  }
}
