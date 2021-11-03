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

package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public class PolymorphCapabilities {

  @CapabilityInject(IPlayerRecipeData.class)
  public static final Capability<IPlayerRecipeData> PLAYER_RECIPE_DATA;

  @CapabilityInject(ITileEntityRecipeData.class)
  public static final Capability<ITileEntityRecipeData> TILE_ENTITY_RECIPE_DATA;

  @CapabilityInject(IStackRecipeData.class)
  public static final Capability<IStackRecipeData> STACK_RECIPE_DATA;

  static {
    PLAYER_RECIPE_DATA = null;
    TILE_ENTITY_RECIPE_DATA = null;
    STACK_RECIPE_DATA = null;
  }

  public static final ResourceLocation PLAYER_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "player_recipe_data");
  // todo: change this id in 1.17 when backwards compatibility isn't an issue
  public static final ResourceLocation TILE_ENTITY_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "persistent_selector");
  public static final ResourceLocation STACK_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "stack_recipe_data");

  public static LazyOptional<IPlayerRecipeData> getRecipeData(PlayerEntity pPlayer) {
    return pPlayer.getCapability(PLAYER_RECIPE_DATA);
  }

  public static LazyOptional<ITileEntityRecipeData> getRecipeData(TileEntity pTileEntity) {
    return pTileEntity.getCapability(TILE_ENTITY_RECIPE_DATA);
  }

  public static LazyOptional<IStackRecipeData> getRecipeData(ItemStack pStack) {
    return pStack.getCapability(STACK_RECIPE_DATA);
  }

  public static void register() {
    CapabilityManager manager = CapabilityManager.INSTANCE;
    manager.register(
        IPlayerRecipeData.class, new Capability.IStorage<IPlayerRecipeData>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<IPlayerRecipeData> capability,
                               IPlayerRecipeData instance, Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<IPlayerRecipeData> capability,
                              IPlayerRecipeData instance, Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptyPlayerRecipeData::new);
    manager.register(
        IStackRecipeData.class, new Capability.IStorage<IStackRecipeData>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<IStackRecipeData> capability,
                               IStackRecipeData instance, Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<IStackRecipeData> capability,
                              IStackRecipeData instance, Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptyStackRecipeData::new);
    manager.register(
        ITileEntityRecipeData.class, new Capability.IStorage<ITileEntityRecipeData>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<ITileEntityRecipeData> capability,
                               ITileEntityRecipeData instance, Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<ITileEntityRecipeData> capability,
                              ITileEntityRecipeData instance, Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptyTileEntityRecipeData::new);
  }

  private static final class EmptyPlayerRecipeData extends EmptyRecipeData<PlayerEntity>
      implements IPlayerRecipeData {

  }

  private static final class EmptyStackRecipeData extends EmptyRecipeData<ItemStack>
      implements IStackRecipeData {

  }

  private static final class EmptyTileEntityRecipeData extends EmptyRecipeData<TileEntity>
      implements ITileEntityRecipeData {

    @Override
    public void tick() {

    }

    @Override
    public boolean isFailing() {
      return false;
    }

    @Override
    public void setFailing(boolean pFailing) {

    }

    @Override
    public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
      return null;
    }
  }

  private static class EmptyRecipeData<E> implements IRecipeData<E> {

    @Override
    public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                              C pInventory,
                                                                              World pWorld,
                                                                              List<T> pRecipes) {
      return Optional.empty();
    }

    @Override
    public Optional<? extends IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe) {

    }

    @Override
    public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {

    }

    @Nonnull
    @Override
    public SortedSet<IRecipePair> getRecipesList() {
      return new TreeSet<>(Comparator.comparing(pair -> pair.getOutput().getDescriptionId()));
    }

    @Override
    public void setRecipesList(@Nonnull SortedSet<IRecipePair> pData) {

    }

    @Override
    public boolean isEmpty(IInventory pInventory) {
      return false;
    }

    @Override
    public Set<ServerPlayerEntity> getListeners() {
      return null;
    }

    @Override
    public void sendRecipesListToListeners(boolean pEmpty) {

    }

    @Override
    public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
      return null;
    }

    @Override
    public E getOwner() {
      return null;
    }

    @Override
    public boolean isFailing() {
      return false;
    }

    @Override
    public void setFailing(boolean pFailing) {

    }

    @Override
    public CompoundNBT writeNBT() {
      return null;
    }

    @Override
    public void readNBT(CompoundNBT pCompound) {

    }
  }
}
