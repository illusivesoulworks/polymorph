package top.theillusivec4.polymorph.common.integration.tomsstorage.network;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import com.tom.storagemod.tile.TileEntityStorageTerminal;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;
import top.theillusivec4.polymorph.mixin.integration.AccessorTomsStorageCrafting;
import top.theillusivec4.polymorph.mixin.integration.AccessorTomsStorageTerminal;

public class CPacketSelectToms {

  private final ResourceLocation recipe;

  public CPacketSelectToms(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSelectToms msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketSelectToms decode(PacketBuffer buf) {
    return new CPacketSelectToms(buf.readResourceLocation());
  }

  public static void handle(CPacketSelectToms msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        World world = sender.getEntityWorld();
        Optional<? extends IRecipe<?>> recipe = world.getRecipeManager().getRecipe(msg.recipe);
        recipe.ifPresent(res -> {
          Container container = sender.openContainer;

          if (container instanceof ContainerCraftingTerminal) {
            TileEntityStorageTerminal te = ((AccessorTomsStorageTerminal) container).getTe();

            if (te instanceof TileEntityCraftingTerminal) {
              TileEntityCraftingTerminal craftTe = (TileEntityCraftingTerminal) te;
              PolymorphCapabilities.getRecipeSelector(craftTe).ifPresent(recipeSelector -> {
                recipeSelector.setSelectedRecipe(res);
                ((AccessorTomsStorageCrafting) craftTe).callOnCraftingMatrixChanged();
              });
            }
          }
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
