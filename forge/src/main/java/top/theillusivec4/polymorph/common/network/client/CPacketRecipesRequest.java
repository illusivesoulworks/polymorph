package top.theillusivec4.polymorph.common.network.client;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.common.impl.PolymorphPacketDistributor;

public class CPacketRecipesRequest {

  public static void encode(CPacketRecipesRequest msg, PacketBuffer buf) {
    // NO-OP
  }

  public static CPacketRecipesRequest decode(PacketBuffer buf) {
    return new CPacketRecipesRequest();
  }

  public static void handle(CPacketRecipesRequest msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        IPolymorphCommon commonApi = PolymorphApi.common();
        commonApi.getProcessorCapability(container).ifPresent(processor -> {
          Set<IRecipeData> recipeDataset = processor.getRecipeDataset();
          ResourceLocation selected = null;

          if (!recipeDataset.isEmpty()) {
            selected = processor.getSelectedRecipe().map(IRecipe::getId)
                .orElse(recipeDataset.stream().findFirst().get().getResourceLocation());
          }
          commonApi.getPacketDistributor().sendRecipesListS2C(sender, recipeDataset, selected);
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
