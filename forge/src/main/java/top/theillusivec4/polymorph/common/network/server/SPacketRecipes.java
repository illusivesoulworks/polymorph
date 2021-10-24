package top.theillusivec4.polymorph.common.network.server;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;
import top.theillusivec4.polymorph.common.impl.RecipeData;

public class SPacketRecipes {

  private final Set<IRecipeData> recipeData;
  private final ResourceLocation selected;

  public SPacketRecipes(Set<IRecipeData> pRecipeData, ResourceLocation pSelected) {
    this.recipeData = new HashSet<>();

    if (pRecipeData != null) {
      this.recipeData.addAll(pRecipeData);
    }
    this.selected = pSelected;
  }

  public static void encode(SPacketRecipes msg, PacketBuffer buf) {

    if (!msg.recipeData.isEmpty()) {
      buf.writeInt(msg.recipeData.size());

      for (IRecipeData data : msg.recipeData) {
        buf.writeResourceLocation(data.getResourceLocation());
        buf.writeItemStack(data.getOutput());
      }

      if (msg.selected != null) {
        buf.writeResourceLocation(msg.selected);
      }
    }
  }

  public static SPacketRecipes decode(PacketBuffer buf) {
    Set<IRecipeData> recipeDataset = new HashSet<>();
    ResourceLocation selected = null;

    if (buf.isReadable()) {
      int size = buf.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipeData(buf.readResourceLocation(), buf.readItemStack()));
      }

      if (buf.isReadable()) {
        selected = buf.readResourceLocation();
      }
    }
    return new SPacketRecipes(recipeDataset, selected);
  }

  public static void handle(SPacketRecipes msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipesWidget.get().ifPresent(widget -> widget.setRecipes(msg.recipeData, msg.selected));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
