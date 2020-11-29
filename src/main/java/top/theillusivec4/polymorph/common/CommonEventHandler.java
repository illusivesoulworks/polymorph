package top.theillusivec4.polymorph.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.common.capability.PersistentSelector;

public class CommonEventHandler {

  @SubscribeEvent
  public void attach(AttachCapabilitiesEvent<TileEntity> evt) {

    if (evt.getObject() instanceof AbstractFurnaceTileEntity) {
      evt.addCapability(PolymorphCapability.PERSISTENT_SELECTOR_ID, new ICapabilityProvider() {
        final LazyOptional<IPersistentSelector> capability =
            LazyOptional.of(() -> new PersistentSelector(evt.getObject()));

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                                 @Nullable Direction side) {
          return PolymorphCapability.PERSISTENT_SELECTOR.orEmpty(cap, this.capability);
        }
      });
    }
  }
}