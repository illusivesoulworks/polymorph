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

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SPacketBlockEntityRecipeSync(BlockPos blockPos,
                                           ResourceLocation selected) {

  public BlockPos getBlockPos() {
    return this.blockPos;
  }

  public ResourceLocation getSelected() {
    return this.selected;
  }

  public static void encode(SPacketBlockEntityRecipeSync packet, FriendlyByteBuf buffer) {
    buffer.writeBlockPos(packet.getBlockPos());
    buffer.writeResourceLocation(packet.getSelected());
  }

  public static SPacketBlockEntityRecipeSync decode(FriendlyByteBuf buffer) {
    return new SPacketBlockEntityRecipeSync(buffer.readBlockPos(), buffer.readResourceLocation());
  }

  public static void handle(SPacketBlockEntityRecipeSync packet) {
    ClientPacketHandler.handle(packet);
  }
}
