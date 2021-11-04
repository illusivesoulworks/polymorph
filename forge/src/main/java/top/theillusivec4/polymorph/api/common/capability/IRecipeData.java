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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface IRecipeData<E> {

  <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(RecipeType<T> pType,
                                                                     C pInventory,
                                                                     Level pWorld,
                                                                     List<T> pRecipes);

  void selectRecipe(@Nonnull Recipe<?> pRecipe);

  Optional<? extends Recipe<?>> getSelectedRecipe();

  void setSelectedRecipe(@Nonnull Recipe<?> pRecipe);

  @Nonnull
  SortedSet<IRecipePair> getRecipesList();

  void setRecipesList(@Nonnull SortedSet<IRecipePair> pData);

  boolean isEmpty(Container pInventory);

  Set<ServerPlayer> getListeners();

  void sendRecipesListToListeners(boolean pEmpty);

  Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData();

  E getOwner();

  boolean isFailing();

  void setFailing(boolean pFailing);

  @Nonnull
  CompoundTag writeNBT();

  void readNBT(CompoundTag pCompound);
}
