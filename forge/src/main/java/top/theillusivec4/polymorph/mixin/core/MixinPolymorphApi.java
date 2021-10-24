package top.theillusivec4.polymorph.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.client.impl.PolymorphClient;
import top.theillusivec4.polymorph.common.impl.PolymorphCommon;

@Mixin(PolymorphApi.class)
public class MixinPolymorphApi {

  @Inject(
      at = @At("HEAD"),
      method = "common",
      remap = false,
      cancellable = true
  )
  private static void polymorph$common(CallbackInfoReturnable<IPolymorphCommon> cir) {
    cir.setReturnValue(PolymorphCommon.get());
  }

  @Inject(
      at = @At("HEAD"),
      method = "client",
      remap = false,
      cancellable = true
  )
  private static void polymorph$client(CallbackInfoReturnable<IPolymorphClient> cir) {
    cir.setReturnValue(PolymorphClient.get());
  }
}
