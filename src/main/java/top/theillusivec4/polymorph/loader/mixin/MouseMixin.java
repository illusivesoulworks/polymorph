package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.client.ClientMixinHooks;

@Mixin(Mouse.class)
public class MouseMixin {

  @Shadow
  MinecraftClient client;
  @Shadow
  double x;
  @Shadow
  double y;

  @Inject(at = @At("HEAD"), method = "method_1611([ZDDI)V", cancellable = true)
  public void mouseClick(boolean[] unused, double mouseX, double mouseY, int button, CallbackInfo cb) {
    Screen screen = this.client.currentScreen;
    double d = this.x * (double) this.client.getWindow().getScaledWidth() / (double) this.client
        .getWindow().getWidth();
    double e = this.y * (double) this.client.getWindow().getScaledHeight() / (double) this.client
        .getWindow().getHeight();

    if (!ClientMixinHooks.clickConflictManager(screen, d, e, button)) {
      cb.cancel();
    }
  }
}
