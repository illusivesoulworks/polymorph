package top.theillusivec4.polymorph.api.common.base;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public interface IPolymorphCommon {

  Optional<ITileEntityRecipeData> tryCreateRecipeData(TileEntity pTileEntity);

  LazyOptional<ITileEntityRecipeData> getRecipeData(TileEntity pTileEntity);

  LazyOptional<ITileEntityRecipeData> getRecipeData(Container pContainer);

  LazyOptional<IPlayerRecipeData> getRecipeData(PlayerEntity pPlayer);

  void registerTileEntity2RecipeData(ITileEntity2RecipeData pTileEntity2RecipeData);

  void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity);

  IPolymorphPacketDistributor getPacketDistributor();

  interface ITileEntity2RecipeData {

    ITileEntityRecipeData createRecipeData(TileEntity pTileEntity);
  }

  interface IContainer2TileEntity {

    TileEntity getTileEntity(Container pContainer);
  }
}
