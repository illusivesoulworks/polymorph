package top.theillusivec4.polymorph.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.PolymorphClient;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.client.impl.PolymorphClientImpl;
import top.theillusivec4.polymorph.common.impl.PolymorphCommonImpl;

@Mixin(PolymorphApi.class)
public class MixinPolymorphApi {

  @Inject(
      at = @At("HEAD"),
      method = "common",
      remap = false,
      cancellable = true
  )
  private static void polymorph$common(CallbackInfoReturnable<PolymorphCommon> cir) {
    cir.setReturnValue(PolymorphCommonImpl.get());
  }

  @Inject(
      at = @At("HEAD"),
      method = "client",
      remap = false,
      cancellable = true
  )
  private static void polymorph$client(CallbackInfoReturnable<PolymorphClient> cir) {
    cir.setReturnValue(PolymorphClientImpl.get());
  }
}
