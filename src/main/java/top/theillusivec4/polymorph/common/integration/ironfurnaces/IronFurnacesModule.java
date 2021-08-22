package top.theillusivec4.polymorph.common.integration.ironfurnaces;

import ironfurnaces.container.BlockIronFurnaceScreenHandlerBase;
import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.FurnaceRecipeController;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class IronFurnacesModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addBlockEntity(blockEntity -> {
      if (blockEntity instanceof BlockIronFurnaceTileBase) {
        return new IronFurnacesRecipeSelector((BlockIronFurnaceTileBase) blockEntity);
      }
      return null;
    }, handledScreen -> {
      if (handledScreen.getScreenHandler() instanceof BlockIronFurnaceScreenHandlerBase) {
        return new FurnaceRecipeController(handledScreen);
      }
      return null;
    });
  }
}
