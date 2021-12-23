package top.theillusivec4.polymorph.common.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.api.common.component.StackRecipeData;

public class PolymorphComponents {

  public static final ComponentKey<PlayerRecipeData> PLAYER_RECIPE_DATA =
      ComponentRegistry.getOrCreate(new Identifier(PolymorphApi.MOD_ID, "player_recipe_data"),
          PlayerRecipeData.class);
  public static final ComponentKey<BlockEntityRecipeData> BLOCK_ENTITY_RECIPE_DATA =
      ComponentRegistry.getOrCreate(new Identifier(PolymorphApi.MOD_ID, "block_entity_recipe_data"),
          BlockEntityRecipeData.class);
  public static final ComponentKey<AbstractStackRecipeData> STACK_RECIPE_DATA =
      ComponentRegistry.getOrCreate(new Identifier(PolymorphApi.MOD_ID, "stack_recipe_data"),
          AbstractStackRecipeData.class);

  public static Optional<PlayerRecipeData> getRecipeData(PlayerEntity pPlayer) {
    return PLAYER_RECIPE_DATA.maybeGet(pPlayer);
  }

  public static Optional<BlockEntityRecipeData> getRecipeData(BlockEntity pBlockEntity) {
    return BLOCK_ENTITY_RECIPE_DATA.maybeGet(pBlockEntity);
  }

  public static Optional<AbstractStackRecipeData> getRecipeData(ItemStack pStack) {
    return STACK_RECIPE_DATA.maybeGet(pStack);
  }
}
