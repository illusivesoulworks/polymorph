package top.theillusivec4.polymorph.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketSetOutput {

  private final ItemStack stack;

  public CPacketSetOutput(ItemStack stack) {
    this.stack = stack;
  }

  public static void encode(CPacketSetOutput msg, PacketBuffer buf) {
    buf.writeItemStack(msg.stack);
  }

  public static CPacketSetOutput decode(PacketBuffer buf) {
    return new CPacketSetOutput(buf.readItemStack());
  }

  public static void handle(CPacketSetOutput msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        if (container instanceof WorkbenchContainer) {
          WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;
          workbenchContainer.getSlot(workbenchContainer.getOutputSlot()).putStack(msg.stack);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
