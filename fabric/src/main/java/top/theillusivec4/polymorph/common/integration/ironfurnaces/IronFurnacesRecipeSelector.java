package top.theillusivec4.polymorph.common.integration.ironfurnaces;

import ironfurnaces.container.BlockIronFurnaceScreenHandler;
import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.common.impl.AbstractFurnaceRecipeSelector;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class IronFurnacesRecipeSelector
    extends AbstractFurnaceRecipeSelector<BlockIronFurnaceTileBase> {

  public IronFurnacesRecipeSelector(BlockIronFurnaceTileBase tileEntity) {
    super(tileEntity);
  }

  @Override
  public void setSelectedRecipe(Recipe<?> recipe) {
    if (recipe instanceof AbstractCookingRecipe) {
      this.selectedRecipe = (AbstractCookingRecipe) recipe;
      World world = this.getParent().getWorld();

      if (world instanceof ServerWorld) {
        ((ServerWorld) world).getPlayers().forEach(player -> {
          if (player.currentScreenHandler instanceof BlockIronFurnaceScreenHandler &&
              player.currentScreenHandler.slots.get(0).inventory == this.getParent()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(recipe.getId());
            ServerPlayNetworking.send(player, PolymorphPackets.HIGHLIGHT_RECIPE, buf);
          }
        });
      }
    }
  }
}
