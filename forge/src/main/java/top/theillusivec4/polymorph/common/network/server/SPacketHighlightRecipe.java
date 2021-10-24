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

  public SPacketHighlightRecipe(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(SPacketHighlightRecipe msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static SPacketHighlightRecipe decode(PacketBuffer buf) {
    return new SPacketHighlightRecipe(buf.readResourceLocation());
  }

  public static void handle(SPacketHighlightRecipe msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipesWidget.get().ifPresent(widget -> widget.highlightRecipe(msg.recipe));
      }
    });
    ctx.get().setPacketHandled(true);
  }

}
