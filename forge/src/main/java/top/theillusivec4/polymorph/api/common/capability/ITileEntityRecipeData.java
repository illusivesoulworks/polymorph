package top.theillusivec4.polymorph.api.common.capability;

import net.minecraft.tileentity.TileEntity;

public interface ITileEntityRecipeData extends IRecipeData<TileEntity> {

  void tick();
}
