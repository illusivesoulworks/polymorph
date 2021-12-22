package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.CommonEventsListener;

@Mixin(value = BlockEntity.class, priority = 1100)
public class MixinBlockEntity {

  @Inject(at = @At("RETURN"), method = "<init>")
  private void polymorph$initBlockEntity(BlockEntityType<?> type, CallbackInfo ci) {
    CommonEventsListener.addTickableBlock((BlockEntity) (Object) this);
  }
}
