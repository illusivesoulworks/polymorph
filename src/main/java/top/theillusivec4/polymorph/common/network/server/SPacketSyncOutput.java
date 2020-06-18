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

package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class SPacketSyncOutput {

  private final ItemStack stack;

  public SPacketSyncOutput(ItemStack stack) {
    this.stack = stack;
  }

  public static void encode(SPacketSyncOutput msg, PacketBuffer buf) {
    buf.writeItemStack(msg.stack);
  }

  public static SPacketSyncOutput decode(PacketBuffer buf) {
    return new SPacketSyncOutput(buf.readItemStack());
  }

  public static void handle(SPacketSyncOutput msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        Container container = clientPlayerEntity.openContainer;
        PolymorphApi.getProvider(container)
            .ifPresent(provider -> provider.getOutputSlot().putStack(msg.stack));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
