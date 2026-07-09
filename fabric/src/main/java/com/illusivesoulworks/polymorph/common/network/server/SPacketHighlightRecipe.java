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

package com.illusivesoulworks.polymorph.common.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SPacketHighlightRecipe(ResourceLocation recipe) {

  public ResourceLocation getRecipe() {
    return this.recipe;
  }

  public static void encode(SPacketHighlightRecipe packet, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(packet.recipe);
  }

  public static SPacketHighlightRecipe decode(FriendlyByteBuf buffer) {
    return new SPacketHighlightRecipe(buffer.readResourceLocation());
  }

  public static void handle(SPacketHighlightRecipe packet) {
    ClientPacketHandler.handle(packet);
  }
}
