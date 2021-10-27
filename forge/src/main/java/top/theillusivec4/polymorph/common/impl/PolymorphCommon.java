package top.theillusivec4.polymorph.common.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;

public class PolymorphCommon implements IPolymorphCommon {

  private static final IPolymorphCommon INSTANCE = new PolymorphCommon();

  public static IPolymorphCommon get() {
    return INSTANCE;
  }

  private final List<ITileEntity2RecipeData> tileEntity2RecipeData = new LinkedList<>();
  private final List<IContainer2TileEntity> container2TileEntities = new LinkedList<>();
  private final IPolymorphPacketDistributor distributor = new PolymorphPacketDistributor();

  @Override
  public IPolymorphPacketDistributor getPacketDistributor() {
    return distributor;
  }

  @Override
  public Optional<ITileEntityRecipeData> tryCreateRecipeData(TileEntity pTileEntity) {

    for (ITileEntity2RecipeData function : this.tileEntity2RecipeData) {
      ITileEntityRecipeData recipeData = function.createRecipeData(pTileEntity);

      if (recipeData != null) {
        return Optional.of(recipeData);
      }
    }
    return Optional.empty();
  }

  @Override
  public LazyOptional<ITileEntityRecipeData> getRecipeData(TileEntity pTileEntity) {
    return PolymorphCapabilities.getRecipeData(pTileEntity);
  }

  @Override
  public LazyOptional<ITileEntityRecipeData> getRecipeData(Container pContainer) {

    for (IContainer2TileEntity function : this.container2TileEntities) {
      TileEntity tileEntity = function.getTileEntity(pContainer);

      if (tileEntity != null) {
        return this.getRecipeData(tileEntity);
      }
    }
    return LazyOptional.empty();
  }

  @Override
  public LazyOptional<IPlayerRecipeData> getRecipeData(PlayerEntity pPlayer) {
    return PolymorphCapabilities.getRecipeData(pPlayer);
  }

  @Override
  public void registerTileEntity2RecipeData(
      ITileEntity2RecipeData pTileEntity2PersistentDataset) {
    this.tileEntity2RecipeData.add(pTileEntity2PersistentDataset);
  }

  @Override
  public void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity) {
    this.container2TileEntities.add(pContainer2TileEntity);
  }
}
