package top.theillusivec4.polymorph.common.integration.fabricfurnaces;

import draylar.fabricfurnaces.entity.BaseFurnaceEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FabricFurnacesModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerBlockEntity2RecipeData(BaseFurnaceEntity.class,
        blockEntity -> new FabricFurnaceRecipeData((BaseFurnaceEntity) blockEntity));
  }
}
