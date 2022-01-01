package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AccessorAbstractFurnaceBlockEntity {

  @Accessor
  NonNullList<ItemStack> getItems();
}
