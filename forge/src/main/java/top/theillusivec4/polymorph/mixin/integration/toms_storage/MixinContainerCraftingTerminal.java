package top.theillusivec4.polymorph.mixin.integration.toms_storage;

import com.tom.storagemod.gui.ContainerCraftingTerminal;
import com.tom.storagemod.gui.ContainerStorageTerminal;
import com.tom.storagemod.tile.TileEntityStorageTerminal;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.mixin.util.integration.TomsStorageHooks;

@Mixin(ContainerCraftingTerminal.class)
public abstract class MixinContainerCraftingTerminal extends ContainerStorageTerminal {

  @Shadow
  @Final
  private List<IContainerListener> listeners;

  public MixinContainerCraftingTerminal(int id, PlayerInventory inv, TileEntityStorageTerminal te) {
    super(id, inv, te);
  }

  @Inject(at = @At("HEAD"), method = "onCraftMatrixChanged", remap = false)
  private void polymorph$onCraftMatrixChanged(CallbackInfo ci) {
    TomsStorageHooks.sendRecipes(this.te, listeners);
  }
}
