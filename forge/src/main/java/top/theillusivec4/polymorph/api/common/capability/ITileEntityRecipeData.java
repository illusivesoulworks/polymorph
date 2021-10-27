package top.theillusivec4.polymorph.api.common.capability;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;

public interface ITileEntityRecipeData extends IRecipeData<TileEntity> {

  void syncRecipesList(ServerPlayerEntity pPlayer);

  boolean isFailing();

  void setFailing(boolean pFailing);
}
