package top.theillusivec4.polymorph.common.network.server;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;

public class SPacketSendRecipes {

  private final Set<ResourceLocation> recipes;
  private final ResourceLocation selected;

  public SPacketSendRecipes(Set<ResourceLocation> recipes, ResourceLocation selected) {
    this.recipes = recipes;
    this.selected = selected;
  }

  public static void encode(SPacketSendRecipes msg, PacketBuffer buf) {

    if (!msg.recipes.isEmpty()) {
      buf.writeResourceLocation(msg.selected);

      for (ResourceLocation recipe : msg.recipes) {
        buf.writeResourceLocation(recipe);
      }
    }
  }

  public static SPacketSendRecipes decode(PacketBuffer buf) {
    Set<ResourceLocation> recipes = new HashSet<>();
    ResourceLocation selected = null;

    if (buf.isReadable()) {
      selected = buf.readResourceLocation();

      while (buf.isReadable()) {
        recipes.add(buf.readResourceLocation());
      }
    }
    return new SPacketSendRecipes(recipes, selected);
  }

  public static void handle(SPacketSendRecipes msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        ResourceLocation finalSelected = msg.selected;
        RecipeControllerHub.getController().ifPresent(
            recipeController -> recipeController.setRecipes(msg.recipes,
                Minecraft.getInstance().world, finalSelected));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
