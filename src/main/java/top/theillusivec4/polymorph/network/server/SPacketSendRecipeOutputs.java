package top.theillusivec4.polymorph.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.ClientCraftingManager;

public class SPacketSendRecipeOutputs {

  private final List<ItemStack> outputs;

  public SPacketSendRecipeOutputs(List<ItemStack> outputs) {
    this.outputs = outputs;
  }

  public static void encode(SPacketSendRecipeOutputs msg, PacketBuffer buf) {
    msg.outputs.forEach(buf::writeItemStack);
  }

  public static SPacketSendRecipeOutputs decode(PacketBuffer buf) {
    List<ItemStack> outputs = new ArrayList<>();

    while (buf.isReadable()) {
      outputs.add(buf.readItemStack());
    }
    return new SPacketSendRecipeOutputs(outputs);
  }

  public static void handle(SPacketSendRecipeOutputs msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

      if (playerEntity != null) {
        ClientCraftingManager.setResultsList(msg.outputs);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
