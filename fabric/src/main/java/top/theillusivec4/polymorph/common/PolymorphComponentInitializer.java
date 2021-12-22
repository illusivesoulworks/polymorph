package top.theillusivec4.polymorph.common;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import java.util.Map;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.common.component.PlayerRecipeDataImpl;
import top.theillusivec4.polymorph.common.component.PolymorphComponents;

public class PolymorphComponentInitializer implements BlockComponentInitializer,
    EntityComponentInitializer, ItemComponentInitializer {

  @Override
  public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
    PolymorphCommon commonApi = PolymorphApi.common();

    for (Map.Entry<Class<? extends BlockEntity>, PolymorphCommon.BlockEntity2RecipeData> entry : commonApi.getAllBlockRecipeData()
        .entrySet()) {
      registry.registerFor(entry.getKey(), PolymorphComponents.BLOCK_ENTITY_RECIPE_DATA,
          (blockEntity) -> entry.getValue().createRecipeData(blockEntity));
    }
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(PlayerEntity.class, PolymorphComponents.PLAYER_RECIPE_DATA,
        PlayerRecipeDataImpl::new);
  }

  @Override
  public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    PolymorphCommon commonApi = PolymorphApi.common();

    for (Map.Entry<Item, PolymorphCommon.Item2RecipeData> entry : commonApi.getAllItemRecipeData()
        .entrySet()) {
      registry.registerFor(entry.getKey(), PolymorphComponents.STACK_RECIPE_DATA,
          (item) -> entry.getValue().createRecipeData(item.getItem()));
    }
  }
}
