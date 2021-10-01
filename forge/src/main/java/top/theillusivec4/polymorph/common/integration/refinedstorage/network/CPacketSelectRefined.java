package top.theillusivec4.polymorph.common.integration.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;

public class CPacketSelectRefined {

  private final ResourceLocation recipe;

  public CPacketSelectRefined(ResourceLocation recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSelectRefined msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
  }

  public static CPacketSelectRefined decode(PacketBuffer buf) {
    return new CPacketSelectRefined(buf.readResourceLocation());
  }

  public static void handle(CPacketSelectRefined msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        World world = sender.getEntityWorld();
        Optional<? extends IRecipe<?>> recipe = world.getRecipeManager().getRecipe(msg.recipe);
        recipe.ifPresent(res -> {
          Container container = sender.openContainer;

          if (container instanceof GridContainer) {
            IGrid grid = ((GridContainer) container).getGrid();

            if (grid instanceof GridNetworkNode) {
              TileEntity te = world.getTileEntity(((GridNetworkNode) grid).getPos());

              if (te instanceof GridTile) {
                PolymorphCapabilities.getRecipeSelector(te).ifPresent(recipeSelector -> {
                  recipeSelector.setSelectedRecipe(res);
                  grid.onCraftingMatrixChanged();
                });
              }
            }
          }
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
