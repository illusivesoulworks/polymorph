package top.theillusivec4.polymorph.mixin.integration;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileEntityCraftingTerminal.class)
public interface AccessorTomsStorageCrafting {

  @Invoker(remap = false)
  void callOnCraftingMatrixChanged();
}
