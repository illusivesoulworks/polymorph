/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class SPacketHighlightRecipe {

  private final String recipe;

  public SPacketHighlightRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(SPacketHighlightRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static SPacketHighlightRecipe decode(PacketBuffer buf) {
    return new SPacketHighlightRecipe(buf.readString(32767));
  }

  public static void handle(SPacketHighlightRecipe msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipeSelectorManager.getSelector()
            .ifPresent(selector -> selector.highlightRecipe(msg.recipe));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
