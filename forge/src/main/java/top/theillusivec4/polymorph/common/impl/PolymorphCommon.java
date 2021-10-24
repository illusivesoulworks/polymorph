package top.theillusivec4.polymorph.common.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;
import top.theillusivec4.polymorph.api.common.capability.IRecipeProcessor;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;

public class PolymorphCommon implements IPolymorphCommon {

  private static final IPolymorphCommon INSTANCE = new PolymorphCommon();

  public static IPolymorphCommon get() {
    return INSTANCE;
  }

  private final List<ITileEntity2Processor> tileEntity2Processors = new LinkedList<>();
  private final List<ITileEntity2Dataset> tileEntity2Datasets = new LinkedList<>();
  private final List<IContainer2TileEntity> container2TileEntities = new LinkedList<>();
  private final IPolymorphPacketDistributor distributor = new PolymorphPacketDistributor();

  @Override
  public IPolymorphPacketDistributor getPacketDistributor() {
    return distributor;
  }

  @Override
  public Optional<IRecipeProcessor> getProcessor(TileEntity pTileEntity) {

    for (ITileEntity2Processor function : this.tileEntity2Processors) {
      IRecipeProcessor controller = function.createProcessor(pTileEntity);

      if (controller != null) {
        return Optional.of(controller);
      }
    }
    return Optional.empty();
  }

  @Override
  public LazyOptional<IRecipeProcessor> getProcessorCapability(TileEntity pTileEntity) {
    return PolymorphCapabilities.getController(pTileEntity);
  }

  @Override
  public LazyOptional<IRecipeProcessor> getProcessorCapability(Container pContainer) {

    for (IContainer2TileEntity function : this.container2TileEntities) {
      TileEntity tileEntity = function.getTileEntity(pContainer);

      if (tileEntity != null) {
        return this.getProcessorCapability(tileEntity);
      }
    }
    return LazyOptional.empty();
  }

  @Override
  public Optional<IRecipeDataset> getDataset(TileEntity pTileEntity) {

    for (ITileEntity2Dataset function : this.tileEntity2Datasets) {
      IRecipeDataset dataset = function.createDataset(pTileEntity);

      if (dataset != null) {
        return Optional.of(dataset);
      }
    }
    return Optional.empty();
  }

  @Override
  public LazyOptional<IRecipeDataset> getDatasetCapability(TileEntity pTileEntity) {
    return PolymorphCapabilities.getRecipeDataCache(pTileEntity);
  }

  @Override
  public LazyOptional<IRecipeDataset> getDatasetCapability(Container pContainer) {

    for (IContainer2TileEntity function : this.container2TileEntities) {
      TileEntity tileEntity = function.getTileEntity(pContainer);

      if (tileEntity != null) {
        return this.getDatasetCapability(tileEntity);
      }
    }
    return LazyOptional.empty();
  }

  @Override
  public void registerTileEntity2Processor(ITileEntity2Processor pTileEntity2Processor) {
    this.tileEntity2Processors.add(pTileEntity2Processor);
  }

  @Override
  public void registerTileEntity2Dataset(ITileEntity2Dataset pTileEntity2Dataset) {
    this.tileEntity2Datasets.add(pTileEntity2Dataset);
  }

  @Override
  public void registerContainer2TileEntity(IContainer2TileEntity pContainer2TileEntity) {
    this.container2TileEntities.add(pContainer2TileEntity);
  }
}
