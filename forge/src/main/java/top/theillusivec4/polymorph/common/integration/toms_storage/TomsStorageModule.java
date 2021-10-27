package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.inventory.CraftingInventory;
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
        CraftingInventory inv =
            (CraftingInventory) PolymorphAccessor.readField(containerScreen.getContainer(),
                "craftMatrix");

        if (inv != null) {
          return clientApi.findCraftingResultSlot(containerScreen)
              .map(slot -> new CraftingTerminalRecipesWidget(containerScreen, inv, slot))
              .orElse(null);
        }
      }
      return null;
    });
  }
}
