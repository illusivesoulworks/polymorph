package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.client.ClientMixinHooks;

@Mixin(Screen.class)
public class ScreenMixin {

  @Inject(at = @At("TAIL"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
  public void init(CallbackInfo cb) {
    @SuppressWarnings("ConstantConditions") Screen screen = (Screen) (Object) this;
    ClientMixinHooks.initConflictManager(screen);
  }
}
