package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.PatternTermContainer;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class CPacketCallUpdate {

  public static void encode(CPacketCallUpdate pPacket, PacketBuffer pBuffer) {

  }

  public static CPacketCallUpdate decode(PacketBuffer pBuffer) {
    return new CPacketCallUpdate();
  }

  public static void handle(CPacketCallUpdate pPacket, Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        if (container instanceof PatternTermContainer) {
          ((AccessorPatternTermContainer) container).callGetAndUpdateOutput();
        }
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
