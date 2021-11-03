package top.theillusivec4.polymorph.mixin.integration.toms_storage;

import com.tom.storagemod.gui.ContainerStorageTerminal;
import com.tom.storagemod.tile.TileEntityStorageTerminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerStorageTerminal.class)
public interface AccessorContainerStorageTerminal {

  @Accessor(remap = false)
  TileEntityStorageTerminal getTe();
}
