package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class CyclicModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof TileCrafter) {
        return new TileCrafterRecipeData((TileCrafter) tileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
      if (container instanceof ContainerCrafter) {
        return (TileCrafter) PolymorphAccessor.readField(container, "tile");
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen instanceof ScreenCrafter &&
          containerScreen.getContainer() instanceof ContainerCrafter) {
        ContainerCrafter containerCrafter = (ContainerCrafter) containerScreen.getContainer();
        return new CrafterRecipesWidget(containerScreen, containerCrafter);
      }
      return null;
    });
  }
}
