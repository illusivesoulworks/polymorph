package top.theillusivec4.polymorph.mixin.util.integration;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import com.tom.storagemod.tile.TileEntityStorageTerminal;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class TomsStorageHooks {

  public static void sendRecipes(TileEntityStorageTerminal tileEntity,
                                 List<IContainerListener> listeners) {

    if (tileEntity instanceof TileEntityCraftingTerminal && tileEntity.getWorld() != null &&
        !tileEntity.getWorld().isRemote()) {
      PolymorphApi.common().getRecipeData(tileEntity).ifPresent(processor -> {
        Set<IRecipePair> dataset = processor.getRecipesList();

        for (IContainerListener listener : listeners) {

          if (listener instanceof ServerPlayerEntity) {
            PolymorphApi.common().getPacketDistributor()
                .sendRecipesListS2C((ServerPlayerEntity) listener, dataset);
          }
        }
      });
    }
  }
}
