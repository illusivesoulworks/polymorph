package top.theillusivec4.polymorph.common.integration.fabricfurnaces;

import draylar.fabricfurnaces.entity.BaseFurnaceEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FabricFurnacesModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addBlockEntity(blockEntity -> {
      if (blockEntity instanceof BaseFurnaceEntity) {
        return new FabricFurnaceRecipeSelector((BaseFurnaceEntity) blockEntity);
      }
      return null;
    }, handledScreen -> null);
  }
}
