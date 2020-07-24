package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.inventory.CraftingResultInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.common.MixinHooks;

@Mixin(CraftingResultInventory.class)
public class CraftingResultInventoryMixin {

  @Inject(at = @At("TAIL"), method = "setStack")
  public void update(CallbackInfo cb) {
    MixinHooks.updateConflictManager();
  }
}
