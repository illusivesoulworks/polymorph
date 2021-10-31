package top.theillusivec4.polymorph.api.common.base;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public interface IPolymorphCommon {

  Optional<ITileEntityRecipeData> tryCreateRecipeData(TileEntity pTileEntity);

  LazyOptional<ITileEntityRecipeData> getRecipeData(TileEntity pTileEntity);

  LazyOptional<ITileEntityRecipeData> getRecipeDataFromTileEntity(Container pContainer);

  Optional<IStackRecipeData> tryCreateRecipeData(ItemStack pStack);

  LazyOptional<IStackRecipeData> getRecipeData(ItemStack pStack);

  LazyOptional<IStackRecipeData> getRecipeDataFromItemStack(Container container);

  LazyOptional<IPlayerRecipeData> getRecipeData(PlayerEntity pPlayer);

  void registerTileEntity2RecipeData(ITileEntity2RecipeData pTileEntity2RecipeData);

  void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity);

  void registerItemStack2RecipeData(IItemStack2RecipeData pItemStack2RecipeData);

  void registerContainer2ItemStack(IContainer2ItemStack pContainer2ItemStack);

  IPolymorphPacketDistributor getPacketDistributor();

  interface IItemStack2RecipeData {

    IStackRecipeData createRecipeData(ItemStack pStack);
  }

  interface ITileEntity2RecipeData {

    ITileEntityRecipeData createRecipeData(TileEntity pTileEntity);
  }

  interface IContainer2TileEntity {

    TileEntity getTileEntity(Container pContainer);
  }

  interface IContainer2ItemStack {

    ItemStack getItemStack(Container pContainer);
  }
}
