package top.theillusivec4.polymorph.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class CommonEventHandler {

  @SubscribeEvent
  public void attach(AttachCapabilitiesEvent<TileEntity> evt) {
    PolymorphApi.getInstance().getSelector(evt.getObject()).ifPresent(selector -> evt
        .addCapability(PolymorphCapability.PERSISTENT_SELECTOR_ID, new Provider(selector)));
  }

  private static class Provider implements ICapabilitySerializable<INBT> {
    final IPersistentSelector selector;
    final LazyOptional<IPersistentSelector> capability;

    public Provider(IPersistentSelector selector) {
      this.selector = selector;
      this.capability = LazyOptional.of(() -> this.selector);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                             @Nullable Direction side) {
      return PolymorphCapability.PERSISTENT_SELECTOR.orEmpty(cap, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapability.PERSISTENT_SELECTOR.writeNBT(this.selector, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      PolymorphCapability.PERSISTENT_SELECTOR.readNBT(this.selector, null, nbt);
    }
  }
}
