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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class SPacketSendRecipes {

  private final List<String> recipes;
  private final String selected;
  private final int length;

  public SPacketSendRecipes(List<String> recipes, String selected) {
    this.recipes = recipes;
    this.selected = selected;
    this.length = recipes.size();
  }

  public static void encode(SPacketSendRecipes msg, PacketBuffer buf) {
    buf.writeInt(msg.length);

    for (String id : msg.recipes) {
      buf.writeString(id);
    }
    buf.writeString(msg.selected);
  }

  public static SPacketSendRecipes decode(PacketBuffer buf) {
    List<String> recipes = new ArrayList<>();
    int length = buf.readInt();

    for (int i = 0; i < length; i++) {
      recipes.add(buf.readString(32767));
    }
    String selected = buf.readString(32767);
    return new SPacketSendRecipes(recipes, selected);
  }

  public static void handle(SPacketSendRecipes msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipeSelectorManager.getSelector().ifPresent(
            selector -> selector
                .setRecipes(new HashSet<>(msg.recipes), clientPlayerEntity.world, true,
                    msg.selected));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
