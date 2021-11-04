package top.theillusivec4.polymorph.common.integration.fabricfurnaces;

import draylar.fabricfurnaces.entity.FabricFurnaceEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FabricFurnacesModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addBlockEntity(blockEntity -> {
      if (blockEntity instanceof FabricFurnaceEntity) {
        return new FabricFurnacesRecipeSelector((FabricFurnaceEntity) blockEntity);
      }
      return null;
    });
  }
}
