package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class SPacketHighlightRecipe {

  private final String recipe;

  public SPacketHighlightRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(SPacketHighlightRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static SPacketHighlightRecipe decode(PacketBuffer buf) {
    return new SPacketHighlightRecipe(buf.readString(32767));
  }

  public static void handle(SPacketHighlightRecipe msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipeSelectorManager.getSelector()
            .ifPresent(selector -> selector.highlightRecipe(msg.recipe));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
