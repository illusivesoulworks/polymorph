package top.theillusivec4.polymorph.loader.impl;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.item.ItemStack;
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
}
