package top.theillusivec4.polymorph.common;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.common.component.PlayerRecipeDataImpl;
import top.theillusivec4.polymorph.common.component.PolymorphComponents;

public class PolymorphComponentInitializer implements BlockComponentInitializer,
    EntityComponentInitializer, ItemComponentInitializer {

  @Override
  public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
    PolymorphCommon commonApi = PolymorphApi.common();
    registry.beginRegistration(BlockEntity.class, PolymorphComponents.BLOCK_ENTITY_RECIPE_DATA)
        .filter(commonApi::hasBlockRecipeData)
        .end(commonApi::createBlockRecipeData);
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(PlayerEntity.class, PolymorphComponents.PLAYER_RECIPE_DATA,
        PlayerRecipeDataImpl::new);
  }

  @Override
  public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    PolymorphCommon commonApi = PolymorphApi.common();
    registry.registerFor(item -> commonApi.hasItemRecipeData(item.getClass()),
        PolymorphComponents.STACK_RECIPE_DATA,
        item -> commonApi.createItemRecipeData(item.getItem()));
  }
}
