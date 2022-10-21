/*
 * Copyright (C) 2020-2022 C4
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

package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.util.BlockEntityTicker;

public class CPacketBlockEntityListener {

  private final boolean add;

  public CPacketBlockEntityListener(boolean pAdd) {
    this.add = pAdd;
  }

  public static void encode(CPacketBlockEntityListener pPacket, PacketBuffer pBuffer) {
    pBuffer.writeBoolean(pPacket.add);
  }

  public static CPacketBlockEntityListener decode(PacketBuffer pBuffer) {
    return new CPacketBlockEntityListener(pBuffer.readBoolean());
  }

  public static void handle(CPacketBlockEntityListener pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {

        if (pPacket.add) {
          Container container = sender.openContainer;
          PolymorphApi.common().getRecipeDataFromTileEntity(container)
              .ifPresent(recipeData -> BlockEntityTicker.add(sender, recipeData));
        } else {
          BlockEntityTicker.remove(sender);
        }
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
