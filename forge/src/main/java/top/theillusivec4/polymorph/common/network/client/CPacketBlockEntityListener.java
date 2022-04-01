package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.util.BlockEntityTicker;

public record CPacketBlockEntityListener(boolean add) {

  public static void encode(CPacketBlockEntityListener pPacket, FriendlyByteBuf pBuffer) {
    pBuffer.writeBoolean(pPacket.add);
  }

  public static CPacketBlockEntityListener decode(FriendlyByteBuf pBuffer) {
    return new CPacketBlockEntityListener(pBuffer.readBoolean());
  }

  public static void handle(CPacketBlockEntityListener pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayer sender = pContext.get().getSender();

      if (sender != null) {

        if (pPacket.add) {
          AbstractContainerMenu container = sender.containerMenu;
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
