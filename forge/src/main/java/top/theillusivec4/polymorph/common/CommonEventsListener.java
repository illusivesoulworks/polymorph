package top.theillusivec4.polymorph.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;
import top.theillusivec4.polymorph.api.common.capability.IRecipeProcessor;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

@SuppressWarnings("unused")
public class CommonEventsListener {

  @SubscribeEvent
  public void openContainer(final PlayerContainerEvent.Open evt) {
    PlayerEntity player = evt.getPlayer();

    if (!player.world.isRemote() && player instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
      Container container = evt.getContainer();

      for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {

        if (integration.openContainer(container, serverPlayerEntity)) {
          return;
        }
      }
    }
  }

  @SubscribeEvent
  public void attachCapabilities(AttachCapabilitiesEvent<TileEntity> evt) {
    IPolymorphCommon commonApi = PolymorphApi.common();
    TileEntity te = evt.getObject();
    commonApi.getProcessor(te).ifPresent(
        processor -> evt.addCapability(PolymorphCapabilities.RECIPE_PROCESSOR_ID,
            new ProcessorProvider(processor)));
    commonApi.getDataset(te).ifPresent(
        dataset -> evt.addCapability(PolymorphCapabilities.RECIPE_DATASET_ID,
            new DatasetProvider(dataset)));
  }

  private static class ProcessorProvider implements ICapabilitySerializable<INBT> {

    final IRecipeProcessor processor;
    final LazyOptional<IRecipeProcessor> capability;

    public ProcessorProvider(IRecipeProcessor processor) {
      this.processor = processor;
      this.capability = LazyOptional.of(() -> this.processor);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                             @Nullable Direction side) {
      return PolymorphCapabilities.RECIPE_PROCESSOR.orEmpty(cap, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapabilities.RECIPE_PROCESSOR.writeNBT(this.processor, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      PolymorphCapabilities.RECIPE_PROCESSOR.readNBT(this.processor, null, nbt);
    }
  }

  private static class DatasetProvider implements ICapabilityProvider {

    final IRecipeDataset dataset;
    final LazyOptional<IRecipeDataset> capability;

    public DatasetProvider(IRecipeDataset dataset) {
      this.dataset = dataset;
      this.capability = LazyOptional.of(() -> this.dataset);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return PolymorphCapabilities.RECIPE_DATASET.orEmpty(cap, this.capability);
    }
  }
}
