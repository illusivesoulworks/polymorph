/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.api.common.base;

import java.util.SortedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IPolymorphPacketDistributor {

  void sendPlayerRecipeSelectionC2S(ResourceLocation resourceLocation);

  void sendPersistentRecipeSelectionC2S(ResourceLocation resourceLocation);

  void sendStackRecipeSelectionC2S(ResourceLocation resourceLocation);

  void sendRecipesListS2C(ServerPlayer player);

  void sendRecipesListS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList);

  void sendRecipesListS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList,
                          ResourceLocation selected);

  void sendHighlightRecipeS2C(ServerPlayer player, ResourceLocation resourceLocation);

  void sendPlayerSyncS2C(ServerPlayer player, SortedSet<IRecipePair> recipesList,
                         ResourceLocation selected);

  void sendBlockEntitySyncS2C(BlockPos blockPos, ResourceLocation selected);

  void sendBlockEntityListenerC2S(boolean add);
}
