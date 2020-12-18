/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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
