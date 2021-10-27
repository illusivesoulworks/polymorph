package top.theillusivec4.polymorph.common.network.server;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public class SPacketRecipesList {

  private final Set<IRecipePair> recipeList;
  private final ResourceLocation selected;

  public SPacketRecipesList(Set<IRecipePair> pRecipeList, ResourceLocation pSelected) {
    this.recipeList = new HashSet<>();

    if (pRecipeList != null) {
      this.recipeList.addAll(pRecipeList);
    }
    this.selected = pSelected;
  }

  public static void encode(SPacketRecipesList pPacket, PacketBuffer pBuffer) {

    if (!pPacket.recipeList.isEmpty()) {
      pBuffer.writeInt(pPacket.recipeList.size());

      for (IRecipePair data : pPacket.recipeList) {
        pBuffer.writeResourceLocation(data.getResourceLocation());
        pBuffer.writeItemStack(data.getOutput());
      }

      if (pPacket.selected != null) {
        pBuffer.writeResourceLocation(pPacket.selected);
      }
    }
  }

  public static SPacketRecipesList decode(PacketBuffer pBuffer) {
    Set<IRecipePair> recipeDataset = new HashSet<>();
    ResourceLocation selected = null;

    if (pBuffer.isReadable()) {
      int size = pBuffer.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePair(pBuffer.readResourceLocation(), pBuffer.readItemStack()));
      }

      if (pBuffer.isReadable()) {
        selected = pBuffer.readResourceLocation();
      }
    }
    return new SPacketRecipesList(recipeDataset, selected);
  }

  public static void handle(SPacketRecipesList pPacket, Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        RecipesWidget.get()
            .ifPresent(widget -> widget.setRecipesList(pPacket.recipeList, pPacket.selected));
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
