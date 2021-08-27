package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceTileEntity.class)
public interface AccessorAbstractFurnaceTileEntity {

  @Accessor
  IRecipeType<? extends AbstractCookingRecipe> getRecipeType();
}
