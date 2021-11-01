package top.theillusivec4.polymorph.common.network.server;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public class SPacketPlayerRecipeSync {

  private final SortedSet<IRecipePair> recipeList;
  private final ResourceLocation selected;

  public SPacketPlayerRecipeSync(SortedSet<IRecipePair> pRecipeList, ResourceLocation pSelected) {
    this.recipeList = new TreeSet<>();

    if (pRecipeList != null) {
      this.recipeList.addAll(pRecipeList);
    }
    this.selected = pSelected;
  }

  public static void encode(SPacketPlayerRecipeSync pPacket, PacketBuffer pBuffer) {

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

  public static SPacketPlayerRecipeSync decode(PacketBuffer pBuffer) {
    SortedSet<IRecipePair> recipeDataset = new TreeSet<>();
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
    return new SPacketPlayerRecipeSync(recipeDataset, selected);
  }

  public static void handle(SPacketPlayerRecipeSync pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {
        PolymorphApi.common().getRecipeData(clientPlayerEntity).ifPresent(recipeData -> {
          recipeData.setRecipesList(pPacket.recipeList);
          clientPlayerEntity.world.getRecipeManager().getRecipe(pPacket.selected).ifPresent(
              recipeData::setSelectedRecipe);
        });
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
