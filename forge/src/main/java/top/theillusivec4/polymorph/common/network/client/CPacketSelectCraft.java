package top.theillusivec4.polymorph.common.network.client;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.craftingstation.CraftingStationModule;
import top.theillusivec4.polymorph.common.integration.fastbench.FastBenchModule;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class CPacketSelectCraft {

  private final ResourceLocation recipe;

  public CPacketSelectCraft(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSelectCraft msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketSelectCraft decode(PacketBuffer buf) {
    return new CPacketSelectCraft(buf.readResourceLocation());
  }

  public static void handle(CPacketSelectCraft msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        CraftingPlayers.add(sender.getUniqueID(), msg.recipe);
        Container container = sender.openContainer;

        if (PolymorphMod.isCraftingStationLoaded) {
          CraftingStationModule.clearRecipe(container);
        }

        if (PolymorphMod.isFastBenchLoaded) {
          FastBenchModule.setLastRecipe(sender, null);
        }
        container.onCraftMatrixChanged(sender.inventory);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
