package top.theillusivec4.polymorph.api.common.component;

import net.minecraft.block.entity.BlockEntity;

public interface BlockEntityRecipeData extends RecipeData<BlockEntity> {

  void tick();
}
