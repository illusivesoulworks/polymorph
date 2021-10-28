package top.theillusivec4.polymorph.api.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.SortedSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface ITileEntityRecipeData extends IRecipeData<TileEntity> {

  void tick();

  boolean isFailing();

  void setFailing(boolean pFailing);

  Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData();
}
