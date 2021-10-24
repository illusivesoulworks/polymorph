package top.theillusivec4.polymorph.api.common.base;

import java.util.Optional;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;
import top.theillusivec4.polymorph.api.common.capability.IRecipeProcessor;

public interface IPolymorphCommon {

  IPolymorphPacketDistributor getPacketDistributor();

  Optional<IRecipeProcessor> getProcessor(TileEntity tileEntity);

  LazyOptional<IRecipeProcessor> getProcessorCapability(TileEntity tileEntity);

  LazyOptional<IRecipeProcessor> getProcessorCapability(Container container);

  Optional<IRecipeDataset> getDataset(TileEntity pTileEntity);

  LazyOptional<IRecipeDataset> getDatasetCapability(TileEntity pTileEntity);

  LazyOptional<IRecipeDataset> getDatasetCapability(Container pContainer);

  void registerTileEntity2Processor(ITileEntity2Processor pTileEntity2Processor);

  void registerTileEntity2Dataset(ITileEntity2Dataset pTileEntity2Dataset);

  void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity);

  interface ITileEntity2Processor {

    IRecipeProcessor createProcessor(TileEntity pTileEntity);
  }

  interface ITileEntity2Dataset {

    IRecipeDataset createDataset(TileEntity pTileEntity);
  }

  interface IContainer2TileEntity {

    TileEntity getTileEntity(Container pContainer);
  }
}
