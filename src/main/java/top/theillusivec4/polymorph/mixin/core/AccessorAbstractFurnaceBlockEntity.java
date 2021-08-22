package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AccessorAbstractFurnaceBlockEntity {

  @Accessor
  RecipeType<? extends AbstractCookingRecipe> getRecipeType();
}
