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
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.common.capability.PlayerRecipeData;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;
import top.theillusivec4.polymorph.mixin.core.AccessorChunkMap;

@SuppressWarnings("unused")
public class CommonEventsListener {

  private static final Set<IBlockEntityRecipeData> BLOCK_ENTITY_RECIPE_DATA = new HashSet<>();

  @SubscribeEvent
  public void registerCapabilities(final RegisterCapabilitiesEvent evt) {
    evt.register(IPlayerRecipeData.class);
    evt.register(IStackRecipeData.class);
    evt.register(IBlockEntityRecipeData.class);
  }

  @SubscribeEvent
  public void serverAboutToStart(final FMLServerAboutToStartEvent evt) {
    PolymorphApi.common().setServer(evt.getServer());
  }

  @SubscribeEvent
  public void serverStopped(final FMLServerStoppedEvent evt) {
    PolymorphApi.common().setServer(null);
    BLOCK_ENTITY_RECIPE_DATA.clear();
  }

  @SubscribeEvent
  public void openContainer(final PlayerContainerEvent.Open pEvent) {
    Player player = pEvent.getPlayer();

    if (!player.level.isClientSide() && player instanceof ServerPlayer serverPlayerEntity) {
      AbstractContainerMenu container = pEvent.getContainer();
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
    Level world = evt.world;

    if (!world.isClientSide() && evt.phase == TickEvent.Phase.END) {
      ChunkSource source = world.getChunkSource();

      if (source instanceof ServerChunkCache cache) {

        for (ChunkHolder chunk : ((AccessorChunkMap) cache.chunkMap).callGetChunks()) {
          LevelChunk levelChunk = chunk.getTickingChunk();

          if (levelChunk != null) {
            ChunkAccess access = chunk.getLastAvailable();

            if (access != null) {
              Set<BlockPos> blockEntities = new HashSet<>(access.getBlockEntitiesPos());

              for (BlockPos pos : blockEntities) {
                BlockEntity be = world.getBlockEntity(pos);

                if (be != null) {
                  PolymorphApi.common().getRecipeData(be).ifPresent(IBlockEntityRecipeData::tick);
                }
              }
            }
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<BlockEntity> pEvent) {
    BlockEntity te = pEvent.getObject();
    PolymorphApi.common().tryCreateRecipeData(te).ifPresent(
        recipeData -> {
          LazyOptional<IBlockEntityRecipeData> cap = LazyOptional.of(() -> recipeData);
          pEvent.addCapability(PolymorphCapabilities.BLOCK_ENTITY_RECIPE_DATA_ID,
              new BlockEntityRecipeDataProvider(cap));
        });
  }

  @SubscribeEvent
  public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> pEvent) {
    Entity entity = pEvent.getObject();

    if (entity instanceof Player) {
      PlayerRecipeData data = new PlayerRecipeData((Player) entity);
      LazyOptional<IPlayerRecipeData> cap = LazyOptional.of(() -> data);
      pEvent.addCapability(PolymorphCapabilities.PLAYER_RECIPE_DATA_ID,
          new PlayerRecipeDataProvider(cap));
    }
  }

  @SubscribeEvent
  public void attachCapabilitiesStack(final AttachCapabilitiesEvent<ItemStack> pEvent) {
    ItemStack stack = pEvent.getObject();
    PolymorphApi.common().tryCreateRecipeData(stack).ifPresent(
        recipeData -> {
          LazyOptional<IStackRecipeData> cap = LazyOptional.of(() -> recipeData);
          pEvent.addCapability(PolymorphCapabilities.STACK_RECIPE_DATA_ID,
              new StackRecipeDataProvider(cap));
        });
  }

  private static class StackRecipeDataProvider implements ICapabilitySerializable<Tag> {

    final LazyOptional<IStackRecipeData> capability;

    public StackRecipeDataProvider(LazyOptional<IStackRecipeData> pCapability) {
      this.capability = pCapability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.STACK_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public Tag serializeNBT() {
      return this.capability.map(IRecipeData::writeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(Tag pNbt) {

      if (pNbt instanceof CompoundTag) {
        this.capability.ifPresent(recipeData -> recipeData.readNBT((CompoundTag) pNbt));
      }
    }
  }

  private static class PlayerRecipeDataProvider implements ICapabilitySerializable<Tag> {

    final LazyOptional<IPlayerRecipeData> capability;

    public PlayerRecipeDataProvider(LazyOptional<IPlayerRecipeData> pCapability) {
      this.capability = pCapability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.PLAYER_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public Tag serializeNBT() {
      return this.capability.map(IRecipeData::writeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(Tag pNbt) {

      if (pNbt instanceof CompoundTag) {
        this.capability.ifPresent(recipeData -> recipeData.readNBT((CompoundTag) pNbt));
      }
    }
  }

  private static class BlockEntityRecipeDataProvider implements ICapabilitySerializable<Tag> {

    final LazyOptional<IBlockEntityRecipeData> capability;

    public BlockEntityRecipeDataProvider(LazyOptional<IBlockEntityRecipeData> pCapability) {
      this.capability = pCapability;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability,
                                             @Nullable Direction pDirection) {
      return PolymorphCapabilities.BLOCK_ENTITY_RECIPE_DATA.orEmpty(pCapability, this.capability);
    }

    @Override
    public Tag serializeNBT() {
      return this.capability.map(IRecipeData::writeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(Tag pNbt) {

      if (pNbt instanceof CompoundTag) {
        this.capability.ifPresent(recipeData -> recipeData.readNBT((CompoundTag) pNbt));
      }
    }
  }
}
