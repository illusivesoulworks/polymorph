package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.capability.SimpleRecipeDataset;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class TinkersConstructModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerTileEntity2Dataset(pTileEntity -> {
      if (pTileEntity instanceof CraftingStationTileEntity) {
        return new SimpleRecipeDataset();
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen.getContainer() instanceof CraftingStationContainer) {
        CraftingStationContainer craftingStationContainer =
            (CraftingStationContainer) containerScreen.getContainer();
        CraftingStationTileEntity tileEntity = craftingStationContainer.getTile();

        if (tileEntity != null) {
          CraftingInventory inv =
              (CraftingInventory) PolymorphAccessor.readField(tileEntity, "craftingInventory");

          if (inv != null) {
            return new CraftingStationRecipesWidget(containerScreen, inv,
                craftingStationContainer.inventorySlots.get(9));
          }
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer && recipe instanceof ICraftingRecipe) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      CraftingStationTileEntity tileEntity = craftingStationContainer.getTile();

      if (tileEntity != null) {
        tileEntity.updateRecipe((ICraftingRecipe) recipe);
        tileEntity.syncToRelevantPlayers(tileEntity::syncRecipe);
      }
      return true;
    }
    return false;
  }
}
