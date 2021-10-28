package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class TomsStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof TileEntityCraftingTerminal) {
        return new CraftingTerminalRecipeData((TileEntityCraftingTerminal) pTileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(pContainer -> {
      if (pContainer instanceof ContainerCraftingTerminal) {
        return (TileEntityCraftingTerminal) PolymorphAccessor.readField(pContainer, "te");
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(containerScreen -> {
      if (containerScreen.getContainer() instanceof ContainerCraftingTerminal) {
        return clientApi.findCraftingResultSlot(containerScreen)
            .map(slot -> new CraftingTerminalRecipesWidget(containerScreen, slot))
            .orElse(null);
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof TileEntityCraftingTerminal) {
      TileEntityCraftingTerminal te = (TileEntityCraftingTerminal) tileEntity;
      PolymorphAccessor.writeField(te, "currentRecipe", recipe);
      PolymorphAccessor.invokeMethod(te, "onCraftingMatrixChanged");
      return true;
    }
    return false;
  }
}
