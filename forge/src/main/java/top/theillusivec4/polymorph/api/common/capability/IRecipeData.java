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

package top.theillusivec4.polymorph.api.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface IRecipeData<E> {

  <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                     C pInventory,
                                                                     World pWorld,
                                                                     List<T> pRecipes);

  void selectRecipe(@Nonnull IRecipe<?> pRecipe);

  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe);

  @Nonnull
  SortedSet<IRecipePair> getRecipesList();

  void setRecipesList(@Nonnull SortedSet<IRecipePair> pData);

  boolean isEmpty(IInventory pInventory);

  Set<ServerPlayerEntity> getListeners();

  void sendRecipesListToListeners(boolean pEmpty);

  Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData();

  E getOwner();

  boolean isFailing();

  void setFailing(boolean pFailing);

  CompoundNBT writeNBT();

  void readNBT(CompoundNBT pCompound);
}
