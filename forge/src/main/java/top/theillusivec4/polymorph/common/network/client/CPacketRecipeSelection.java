package top.theillusivec4.polymorph.common.network.client;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class CPacketRecipeSelection {

  private final ResourceLocation recipe;

  public CPacketRecipeSelection(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketRecipeSelection msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketRecipeSelection decode(PacketBuffer buf) {
    return new CPacketRecipeSelection(buf.readResourceLocation());
  }

  public static void handle(CPacketRecipeSelection msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        World world = sender.getEntityWorld();
        Optional<? extends IRecipe<?>> recipe = world.getRecipeManager().getRecipe(msg.recipe);
        recipe.ifPresent(res -> {
          Container container = sender.openContainer;
          PolymorphApi.common().getProcessorCapability(container)
              .ifPresent(controller -> controller.setSelectedRecipe(res, sender));
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
