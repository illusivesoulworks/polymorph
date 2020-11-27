package top.theillusivec4.polymorph.common.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.polymorph.client.RecipeSelectorManager;

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
        RecipeManager manager = clientPlayerEntity.world.getRecipeManager();
        List<ICraftingRecipe> recipes = new ArrayList<>();
        msg.recipes.forEach(id -> manager.getRecipe(new ResourceLocation(id)).ifPresent(recipe -> {

          if (recipe instanceof ICraftingRecipe) {
            recipes.add((ICraftingRecipe) recipe);
          }
        }));
        RecipeSelectorManager.getSelector().ifPresent(
            selectionManager -> selectionManager
                .setRecipes(recipes, clientPlayerEntity.world, true));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
