package top.theillusivec4.polymorph.loader.mixin.integration;

import java.util.function.Supplier;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.widgets.Button;
import me.shedaniel.rei.impl.InternalWidgets;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.common.integration.rei.ReiMixinHooks;

@Mixin(InternalWidgets.class)
public class ReiMixin {

  @Inject(at = @At("HEAD"), method = "lambda$createAutoCraftingButtonWidget$0(Lnet/minecraft/client/gui/screen/ingame/HandledScreen;Ljava/util/function/Supplier;Lme/shedaniel/rei/api/widgets/Button;)V", remap = false)
  private static void _polymorph_hookTransfer(HandledScreen<?> screen,
                                              Supplier<RecipeDisplay> recipeDisplaySupplier,
                                              Button button, CallbackInfo cb) {
    ReiMixinHooks.setRecipe(recipeDisplaySupplier.get());
  }
}
