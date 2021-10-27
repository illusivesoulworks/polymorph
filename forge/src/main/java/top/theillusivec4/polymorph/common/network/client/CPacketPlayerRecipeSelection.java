package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CPacketPlayerRecipeSelection {

  private final ResourceLocation recipe;

  public CPacketPlayerRecipeSelection(ResourceLocation pResourceLocation) {
    this.recipe = pResourceLocation;
  }

  public static void encode(CPacketPlayerRecipeSelection pPacket, PacketBuffer pBuffer) {
    pBuffer.writeResourceLocation(pPacket.recipe);
  }

  public static CPacketPlayerRecipeSelection decode(PacketBuffer pBuffer) {
    return new CPacketPlayerRecipeSelection(pBuffer.readResourceLocation());
  }

  public static void handle(CPacketPlayerRecipeSelection pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        sender.world.getRecipeManager().getRecipe(pPacket.recipe).ifPresent(recipe -> {
          PolymorphApi.common().getRecipeData(sender)
              .ifPresent(dataset -> dataset.setSelectedRecipe(recipe));

          for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {

            if (integration.selectRecipe(container, recipe)) {
              return;
            }
          }
          container.onCraftMatrixChanged(sender.inventory);
        });
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
