package top.theillusivec4.polymorph.common.integration.refinedstorageaddons;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class RefinedStorageAddonsModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof GridContainer) {
      IGrid grid = ((GridContainer) container).getGrid();

      if (grid instanceof WirelessCraftingGrid) {
        PolymorphAccessor.writeField(grid, "currentRecipe", recipe);
        grid.onCraftingMatrixChanged();
      }
      return true;
    }
    return false;
  }
}
