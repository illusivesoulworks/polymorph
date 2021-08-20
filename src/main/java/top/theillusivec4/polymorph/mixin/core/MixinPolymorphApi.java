package top.theillusivec4.polymorph.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.impl.PolymorphApiImpl;

@Mixin(PolymorphApi.class)
public class MixinPolymorphApi {

  @Inject(at = @At("HEAD"), method = "getInstance", cancellable = true, remap = false)
  private static void polymorph$getInstance(CallbackInfoReturnable<PolymorphApi> cir) {
    cir.setReturnValue(PolymorphApiImpl.INSTANCE);
  }
}
