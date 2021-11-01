package top.theillusivec4.polymorph.common.integration.ironfurnaces;

import ironfurnaces.container.BlockIronFurnaceContainerBase;
import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.client.recipe.widget.FurnaceRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class IronFurnacesModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof BlockIronFurnaceTileBase) {
        return new IronFurnaceRecipeData((BlockIronFurnaceTileBase) pTileEntity);
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen.getContainer() instanceof BlockIronFurnaceContainerBase) {
        return new FurnaceRecipesWidget(pContainerScreen);
      }
      return null;
    });
  }
}
