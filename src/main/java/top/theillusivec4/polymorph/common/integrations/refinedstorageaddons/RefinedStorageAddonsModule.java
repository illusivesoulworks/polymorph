package top.theillusivec4.polymorph.common.integrations.refinedstorageaddons;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.container.GridContainer;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class RefinedStorageAddonsModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addProvider(GridContainer.class, GridProvider::new);
  }

  public static class GridProvider implements PolyProvider {

    GridContainer gridContainer;

    public GridProvider(GridContainer gridContainer) {
      this.gridContainer = gridContainer;
    }

    @Override
    public boolean isValid() {
      GridType gridType = this.gridContainer.getGrid().getGridType();
      return gridType == GridType.CRAFTING || gridType == GridType.PATTERN;
    }

    @Override
    public Container getContainer() {
      return this.gridContainer;
    }

    @Nonnull
    @Override
    public CraftingInventory getCraftingInventory() {
      CraftingInventory craftingInventory = gridContainer.getGrid().getCraftingMatrix();
      return craftingInventory != null ? craftingInventory :
          new CraftingInventory(this.gridContainer, 0, 0);
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {

      for (Slot slot : gridContainer.inventorySlots) {

        if (slot.inventory instanceof CraftResultInventory) {
          return slot;
        }
      }
      return gridContainer.getSlot(0);
    }
  }
}
