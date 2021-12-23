package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingRecipe.class)
public interface AccessorSmithingRecipe {

  @Accessor
  Ingredient getBase();

  @Accessor
  Ingredient getAddition();
}
