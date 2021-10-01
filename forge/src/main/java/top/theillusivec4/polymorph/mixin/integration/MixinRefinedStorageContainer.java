package top.theillusivec4.polymorph.mixin.integration;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.BaseTile;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.mixin.core.AccessorContainer;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

@Mixin(GridContainer.class)
public abstract class MixinRefinedStorageContainer extends BaseContainer {

  @Shadow
  @Final
  private IGrid grid;

  protected MixinRefinedStorageContainer(@Nullable ContainerType<?> type, @Nullable BaseTile tile,
                                         PlayerEntity player, int windowId) {
    super(type, tile, player, windowId);
  }

  @Inject(at = @At("HEAD"), method = "onCraftingMatrixChanged", remap = false)
  private void polymorph$onCraftingMatrixChanged(CallbackInfo ci) {
    RefinedStorageHooks.sendRecipes(this.getPlayer().getEntityWorld(), this.grid,
        ((AccessorContainer) this).getListeners());
  }
}
