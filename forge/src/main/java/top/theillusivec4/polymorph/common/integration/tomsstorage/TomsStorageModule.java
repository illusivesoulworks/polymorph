package top.theillusivec4.polymorph.common.integration.tomsstorage;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class TomsStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addTileEntity(tileEntity -> {
      if (tileEntity instanceof TileEntityCraftingTerminal) {
        return new TomsStorageRecipeSelector((TileEntityCraftingTerminal) tileEntity);
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphClientApi.getInstance().addRecipeController(containerScreen -> {
      if (containerScreen.getContainer() instanceof ContainerCraftingTerminal) {
        Slot resultSlot = null;
        CraftingInventory craftingInventory = null;
        Container container = containerScreen.getContainer();

        for (Slot slot : container.inventorySlots) {

          if (resultSlot == null && slot.inventory instanceof CraftResultInventory) {
            resultSlot = slot;
          } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
            craftingInventory = (CraftingInventory) slot.inventory;
          }

          if (resultSlot != null && craftingInventory != null) {
            break;
          }
        }
        return resultSlot != null && craftingInventory != null ?
            new TomsStorageRecipeController(containerScreen, craftingInventory, resultSlot) : null;
      }
      return null;
    });
  }
}
