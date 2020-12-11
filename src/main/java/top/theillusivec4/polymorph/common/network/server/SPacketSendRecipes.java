package top.theillusivec4.polymorph.common.network.server;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class SPacketSendRecipes {

  private final List<String> recipes;

  public SPacketSendRecipes(List<String> recipes) {
    this.recipes = recipes;
  }

  public static void encode(SPacketSendRecipes msg, PacketBuffer buf) {

    for (String id : msg.recipes) {
      buf.writeString(id);
    }
  }

  public static SPacketSendRecipes decode(PacketBuffer buf) {
    List<String> recipes = new ArrayList<>();

    while (buf.isReadable()) {
      recipes.add(buf.readString(32767));
    }
    return new SPacketSendRecipes(recipes);
  }

  public static void handle(SPacketSendRecipes msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipeSelectorManager.getSelector().ifPresent(
            selector -> selector
                .setRecipes(new HashSet<>(msg.recipes), clientPlayerEntity.world, true));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
