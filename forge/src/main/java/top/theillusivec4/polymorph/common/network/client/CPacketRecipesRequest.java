package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class CPacketRecipesRequest {

  public static void encode(CPacketRecipesRequest pPacket, PacketBuffer pBuffer) {
    // NO-OP
  }

  @SuppressWarnings("utility")
  public static CPacketRecipesRequest decode(PacketBuffer pBuffer) {
    return new CPacketRecipesRequest();
  }

  public static void handle(CPacketRecipesRequest pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        PolymorphApi.common().getRecipeData(container)
            .ifPresent(recipeData -> recipeData.syncRecipesList(sender));
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
