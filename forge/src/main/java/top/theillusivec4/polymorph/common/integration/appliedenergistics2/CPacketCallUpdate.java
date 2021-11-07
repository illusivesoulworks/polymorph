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

package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.PatternTermMenu;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermMenu;

public class CPacketCallUpdate {

  public static void encode(CPacketCallUpdate pPacket, FriendlyByteBuf pBuffer) {

  }

  public static CPacketCallUpdate decode(FriendlyByteBuf pBuffer) {
    return new CPacketCallUpdate();
  }

  public static void handle(CPacketCallUpdate pPacket, Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayer sender = pContext.get().getSender();

      if (sender != null) {
        AbstractContainerMenu container = sender.containerMenu;

        if (container instanceof PatternTermMenu) {
          ((AccessorPatternTermMenu) container).callGetAndUpdateOutput();
        }
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
