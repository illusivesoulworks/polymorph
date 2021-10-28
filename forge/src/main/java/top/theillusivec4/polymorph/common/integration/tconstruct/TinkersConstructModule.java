package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class TinkersConstructModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof CraftingStationTileEntity) {
        return new CraftingStationRecipeData((CraftingStationTileEntity) pTileEntity);
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
        return new CraftingStationRecipesWidget(containerScreen,
            craftingStationContainer.inventorySlots.get(9));
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
