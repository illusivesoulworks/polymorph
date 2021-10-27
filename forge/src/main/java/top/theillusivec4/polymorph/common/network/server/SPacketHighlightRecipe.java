package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

public class SPacketHighlightRecipe {

  private final ResourceLocation recipe;

  public SPacketHighlightRecipe(ResourceLocation pResourceLocation) {
    this.recipe = pResourceLocation;
  }

  public static void encode(SPacketHighlightRecipe pPacket, PacketBuffer pBuffer) {
    pBuffer.writeResourceLocation(pPacket.recipe);
  }

  public static SPacketHighlightRecipe decode(PacketBuffer pBuffer) {
    return new SPacketHighlightRecipe(pBuffer.readResourceLocation());
  }

  public static void handle(SPacketHighlightRecipe pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipesWidget.get().ifPresent(widget -> widget.highlightRecipe(pPacket.recipe));
      }
    });
    pContext.get().setPacketHandled(true);
  }

}
