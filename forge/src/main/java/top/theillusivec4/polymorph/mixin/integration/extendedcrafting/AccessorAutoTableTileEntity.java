package top.theillusivec4.polymorph.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AutoTableTileEntity.class)
public interface AccessorAutoTableTileEntity {

  @Accessor(remap = false)
  boolean getIsGridChanged();

  @Accessor(remap = false)
  void setRecipe(AutoTableTileEntity.WrappedRecipe recipe);
}
