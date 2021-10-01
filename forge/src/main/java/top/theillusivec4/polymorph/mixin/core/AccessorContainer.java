package top.theillusivec4.polymorph.mixin.core;

import java.util.List;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Container.class)
public interface AccessorContainer {

  @Accessor
  List<IContainerListener> getListeners();
}
