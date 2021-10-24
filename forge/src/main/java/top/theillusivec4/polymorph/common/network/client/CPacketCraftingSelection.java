package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.crafting.CraftingPlayers;

public class CPacketCraftingSelection {

  private final ResourceLocation recipe;

  public CPacketCraftingSelection(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketCraftingSelection msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketCraftingSelection decode(PacketBuffer buf) {
    return new CPacketCraftingSelection(buf.readResourceLocation());
  }

  public static void handle(CPacketCraftingSelection msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        ResourceLocation rl = msg.recipe;
        CraftingPlayers.add(sender.getUniqueID(), rl);
        Container container = sender.openContainer;
        sender.world.getRecipeManager().getRecipe(rl).ifPresent(recipe -> {
          for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {
            if (integration.selectRecipe(container, recipe)) {
              return;
            }
          }
        });
        container.onCraftMatrixChanged(sender.inventory);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
