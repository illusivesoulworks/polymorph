package top.theillusivec4.polymorph.common.integration.cyclic.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;

public class SPacketCrafterRecipes {

  private final Set<ResourceLocation> recipes;

  public SPacketCrafterRecipes(Set<ResourceLocation> recipes) {
    this.recipes = recipes;
  }

  public static void encode(SPacketCrafterRecipes msg, PacketBuffer buf) {

    for (ResourceLocation recipe : msg.recipes) {
      buf.writeResourceLocation(recipe);
    }
  }

  public static SPacketCrafterRecipes decode(PacketBuffer buf) {
    Set<ResourceLocation> recipes = new HashSet<>();

    while (buf.isReadable()) {
      recipes.add(buf.readResourceLocation());
    }
    return new SPacketCrafterRecipes(recipes);
  }

  public static void handle(SPacketCrafterRecipes msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipeControllerHub.getController().ifPresent(
            recipeController -> recipeController.setRecipes(msg.recipes,
                Minecraft.getInstance().world, new ResourceLocation("")));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
