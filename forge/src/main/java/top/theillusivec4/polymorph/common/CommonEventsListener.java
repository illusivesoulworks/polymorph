/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.common;

import com.mojang.datafixers.util.Pair;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;
import top.theillusivec4.polymorph.common.capability.PlayerRecipeData;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

@SuppressWarnings("unused")
public class CommonEventsListener {

  @SubscribeEvent
  public void serverAboutToStart(final FMLServerAboutToStartEvent evt) {
    PolymorphApi.common().setServer(evt.getServer());
  }

  @SubscribeEvent
  public void serverStopped(final FMLServerStoppedEvent evt) {
    PolymorphApi.common().setServer(null);
  }

  @SubscribeEvent
  public void openContainer(final PlayerContainerEvent.Open pEvent) {
    PlayerEntity player = pEvent.getPlayer();

    if (!player.level.isClientSide() && player instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
      Container container = pEvent.getContainer();
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeDataFromTileEntity(container).ifPresent(
          recipeData -> {
            IPolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();

            if (recipeData.isFailing() || recipeData.isEmpty(null)) {
              packetDistributor.sendRecipesListS2C(serverPlayerEntity);
            } else {
              Pair<SortedSet<IRecipePair>, ResourceLocation> data = recipeData.getPacketData();
              packetDistributor.sendRecipesListS2C(serverPlayerEntity, data.getFirst(),
                  data.getSecond());
            }
          });

      for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

        if (integration.openContainer(container, serverPlayerEntity)) {
          return;
        }
      }
    }
  }

  @SubscribeEvent
  public void worldTick(final TickEvent.WorldTickEvent evt) {
    World world = evt.world;

    if (!world.isClientSide() && evt.phase == TickEvent.Phase.END) {

      for (TileEntity tileEntity : world.blockEntityList) {
        PolymorphApi.common().getRecipeData(tileEntity)
            .ifPresent(ITileEntityRecipeData::tick);
      }
    }
  }

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<TileEntity> pEvent) {
    TileEntity te = pEvent.getObject();
    PolymorphApi.common().tryCreateRecipeData(te).ifPresent(
        recipeData -> pEvent.addCapability(PolymorphCapabilities.TILE_ENTITY_RECIPE_DATA_ID,
            new TileEntityRecipeDataProvider(recipeData)));
  }

  @SubscribeEvent
  public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> pEvent) {
    Entity entity = pEvent.getObject();

    if (entity instanceof PlayerEntity) {
      PlayerRecipeData data = new PlayerRecipeData((PlayerEntity) entity);
      pEvent.addCapability(PolymorphCapabilities.PLAYER_RECIPE_DATA_ID,
          new PlayerRecipeDataProvider(data));
    }
  }

  @SubscribeEvent
  public void attachCapabilitiesStack(final AttachCapabilitiesEvent<ItemStack> pEvent) {
    ItemStack stack = pEvent.getObject();
    PolymorphApi.common().tryCreateRecipeData(stack).ifPresent(
        recipeData -> pEvent.addCapability(PolymorphCapabilities.STACK_RECIPE_DATA_ID,
            new StackRecipeDataProvider(recipeData)));
  }

  private static class StackRecipeDataProvider implements ICapabilitySerializable<INBT> {

    final IStackRecipeData recipeData;
    final LazyOptional<IStackRecipeData> capability;

    public StackRecipeDataProvider(IStackRecipeData pRecipeData) {
      this.recipeData = pRecipeData;
      this.capability = LazyOptional.of(() -> this.recipeData);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.STACK_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapabilities.STACK_RECIPE_DATA.writeNBT(this.recipeData, null);
    }

    @Override
    public void deserializeNBT(INBT pNbt) {
      PolymorphCapabilities.STACK_RECIPE_DATA.readNBT(this.recipeData, null, pNbt);
    }
  }

  private static class PlayerRecipeDataProvider implements ICapabilitySerializable<INBT> {

    final IPlayerRecipeData recipeData;
    final LazyOptional<IPlayerRecipeData> capability;

    public PlayerRecipeDataProvider(IPlayerRecipeData pRecipeData) {
      this.recipeData = pRecipeData;
      this.capability = LazyOptional.of(() -> this.recipeData);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.PLAYER_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapabilities.PLAYER_RECIPE_DATA.writeNBT(this.recipeData, null);
    }

    @Override
    public void deserializeNBT(INBT pNbt) {
      PolymorphCapabilities.PLAYER_RECIPE_DATA.readNBT(this.recipeData, null, pNbt);
    }
  }

  private static class TileEntityRecipeDataProvider implements ICapabilitySerializable<INBT> {

    final ITileEntityRecipeData recipeData;
    final LazyOptional<ITileEntityRecipeData> capability;

    public TileEntityRecipeDataProvider(ITileEntityRecipeData pRecipeData) {
      this.recipeData = pRecipeData;
      this.capability = LazyOptional.of(() -> this.recipeData);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.TILE_ENTITY_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapabilities.TILE_ENTITY_RECIPE_DATA.writeNBT(this.recipeData, null);
    }

    @Override
    public void deserializeNBT(INBT pNbt) {
      PolymorphCapabilities.TILE_ENTITY_RECIPE_DATA.readNBT(this.recipeData, null, pNbt);
    }
  }
}
