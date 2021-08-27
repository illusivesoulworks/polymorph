package top.theillusivec4.polymorph.common.network.client;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;

public class CPacketSelectPersist {

  private final ResourceLocation recipe;

  public CPacketSelectPersist(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSelectPersist msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketSelectPersist decode(PacketBuffer buf) {
    return new CPacketSelectPersist(buf.readResourceLocation());
  }

  public static void handle(CPacketSelectPersist msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        World world = sender.getEntityWorld();
        Optional<? extends IRecipe<?>> recipe = world.getRecipeManager().getRecipe(msg.recipe);
        recipe.ifPresent(res -> {
          Container container = sender.openContainer;
          IInventory inventory = container.inventorySlots.get(0).inventory;

          if (inventory instanceof TileEntity) {
            PolymorphCapabilities.getRecipeSelector((TileEntity) inventory)
                .ifPresent(recipeSelector -> recipeSelector.setSelectedRecipe(res));
          }
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
