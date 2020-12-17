package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceContainer.class)
public interface AbstractFurnaceContainerMixin {

  @Accessor
  IInventory getFurnaceInventory();
}
