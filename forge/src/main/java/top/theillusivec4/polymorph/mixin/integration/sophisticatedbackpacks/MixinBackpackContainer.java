package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.mixin.util.integration.SophisticatedBackpacksMixinHooks;

@Mixin(BackpackContainer.class)
public class MixinBackpackContainer {

  @Shadow(remap = false)
  @Final
  PlayerEntity player;

  @Shadow(remap = false)
  @Final
  private Map<Integer, UpgradeContainerBase<?, ?>> upgradeContainers;

  @Inject(
      at = @At("RETURN"),
      method = "setOpenTabId(I)V",
      remap = false
  )
  private void polymorph$setOpenTabId(int id, CallbackInfo ci) {
    SophisticatedBackpacksMixinHooks.onOpenTab(id, this.player, this.upgradeContainers);
  }
}
