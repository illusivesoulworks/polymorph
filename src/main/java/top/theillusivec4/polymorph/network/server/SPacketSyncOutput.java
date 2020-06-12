package top.theillusivec4.polymorph.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.Polymorph;

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
    ctx.get().enqueueWork(
        () -> {
          ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

          if (clientPlayerEntity != null) {
            Container container = clientPlayerEntity.openContainer;

            if (container instanceof WorkbenchContainer) {
              WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;
              Polymorph.LOGGER.info("Syncing output " + msg.stack.toString());
              workbenchContainer.getSlot(workbenchContainer.getOutputSlot()).putStack(msg.stack);
            }
          }
        });
    ctx.get().setPacketHandled(true);
  }
}
