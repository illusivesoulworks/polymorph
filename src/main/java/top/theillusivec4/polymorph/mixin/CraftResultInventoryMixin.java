package top.theillusivec4.polymorph.mixin;

import net.minecraft.inventory.CraftResultInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.MixinHooks;

@Mixin(CraftResultInventory.class)
public class CraftResultInventoryMixin {

  @Inject(at = @At("HEAD"), method = "setInventorySlotContents")
  private void _polymorph_sendUpdate(CallbackInfo cb) {
    MixinHooks.sendUpdate();
  }
}
