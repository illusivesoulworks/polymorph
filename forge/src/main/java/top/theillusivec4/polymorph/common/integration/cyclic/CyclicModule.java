package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.inventory.CraftingInventory;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class CyclicModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2Processor(tileEntity -> {
      if (tileEntity instanceof TileCrafter) {
        return new TileCrafterRecipeProcessor((TileCrafter) tileEntity);
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
        TileCrafter crafter = (TileCrafter) PolymorphAccessor.readField(containerCrafter, "tile");
        return new CrafterRecipesWidget(containerScreen, containerCrafter,
            (CraftingInventory) PolymorphAccessor.readField(crafter, "craftMatrix"));
      }
      return null;
    });
  }
}
