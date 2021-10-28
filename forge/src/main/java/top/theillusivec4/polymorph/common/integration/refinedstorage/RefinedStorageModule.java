package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageMixinHooks;

public class RefinedStorageModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(pContainerScreen -> {
      if (pContainerScreen.getContainer() instanceof GridContainer) {
        GridContainer container = (GridContainer) pContainerScreen.getContainer();

        if (container.getTile() instanceof GridTile) {
          return clientApi.findCraftingResultSlot(pContainerScreen).map(
              slot -> new GridTileRecipesWidget(pContainerScreen, slot)).orElse(null);
        }
      }
      return null;
    });
  }

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof GridTile) {
        return new GridTileRecipeData((GridTile) pTileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(pContainer -> {
      if (pContainer instanceof GridContainer) {
        IGrid grid = ((GridContainer) pContainer).getGrid();

        if (grid instanceof GridNetworkNode) {
          GridNetworkNode gridNetworkNode = (GridNetworkNode) grid;
          return gridNetworkNode.getWorld().getTileEntity(gridNetworkNode.getPos());
        }
      }
      return null;
    });
    MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
  }

  private void serverStarted(final FMLServerStartedEvent evt) {
    RefinedStorageMixinHooks.loaded = true;
  }

  private void serverStopped(final FMLServerStoppedEvent evt) {
    RefinedStorageMixinHooks.loaded = false;
  }

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof GridTile) {
      IGrid grid = ((GridTile) tileEntity).getNode();

      if (grid instanceof GridNetworkNode) {
        PolymorphAccessor.writeField(grid, "currentRecipe", recipe);
        grid.onCraftingMatrixChanged();
      }
      return true;
    }
    return false;
  }
}
