package top.theillusivec4.polymorph.common.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceContainer;

public class FurnaceRecipeData extends AbstractTileEntityRecipeData<AbstractFurnaceTileEntity> {

  public FurnaceRecipeData(AbstractFurnaceTileEntity pOwner) {
    super(pOwner);
  }

  @Override
  public boolean isEmpty() {
    return this.getOwner().getStackInSlot(0).isEmpty();
  }

  @Override
  public List<ServerPlayerEntity> getListeningPlayers() {
    World world = this.getOwner().getWorld();

    if (world instanceof ServerWorld) {
      return ((ServerWorld) world).getPlayers().stream().filter(
          player -> (player.openContainer instanceof AbstractFurnaceContainer &&
              ((AccessorAbstractFurnaceContainer) player.openContainer).getFurnaceInventory() ==
                  this.getOwner())).collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
}
