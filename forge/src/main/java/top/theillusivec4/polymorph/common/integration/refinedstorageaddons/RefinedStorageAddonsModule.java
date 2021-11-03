package top.theillusivec4.polymorph.common.integration.refinedstorageaddons;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.refinedstorage.AccessorGrid;

public class RefinedStorageAddonsModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe && container instanceof GridContainer) {
      IGrid grid = ((GridContainer) container).getGrid();

      if (grid instanceof WirelessCraftingGrid) {
        ((AccessorGrid) grid).setCurrentRecipe((ICraftingRecipe) recipe);
        grid.onCraftingMatrixChanged();
        return true;
      }
    }
    return false;
  }
}
