package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ingredient.class)
public abstract class IngredientMixin {

  @Inject(at = @At("RETURN"), method = "<init>*")
  public void construct(CallbackInfo cb) {
    this.cacheMatchingStacks();
  }

  @Shadow
  public abstract void cacheMatchingStacks();
}
