package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.common.MixinHooks;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin {

  @Inject(at = @At("RETURN"), method = "<init>")
  public void init(CallbackInfo cb) {
    MixinHooks.cacheIngredients(this.getPreviewInputs());
  }

  @Shadow
  public abstract DefaultedList<Ingredient> getPreviewInputs();
}
