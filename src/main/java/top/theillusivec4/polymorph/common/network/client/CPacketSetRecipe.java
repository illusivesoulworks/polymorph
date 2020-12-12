package top.theillusivec4.polymorph.common.network.client;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.mixin.AbstractFurnaceContainerMixin;

public class CPacketSetRecipe {

  private final String recipe;

  public CPacketSetRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSetRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static CPacketSetRecipe decode(PacketBuffer buf) {
    return new CPacketSetRecipe(buf.readString(32767));
  }

  public static void handle(CPacketSetRecipe msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        if (container instanceof AbstractFurnaceContainer) {
          AbstractFurnaceContainer furnaceContainer = (AbstractFurnaceContainer) container;
          IInventory inventory =
              ((AbstractFurnaceContainerMixin) furnaceContainer).getFurnaceInventory();

          if (inventory instanceof AbstractFurnaceTileEntity) {
            AbstractFurnaceTileEntity te = (AbstractFurnaceTileEntity) inventory;
            te.getCapability(PolymorphCapability.PERSISTENT_SELECTOR).ifPresent(selector -> {
              Optional<? extends IRecipe<?>> recipe = sender.getServerWorld().getRecipeManager()
                  .getRecipe(new ResourceLocation(msg.recipe));
              recipe.ifPresent(selector::setSelectedRecipe);
            });
          }
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
