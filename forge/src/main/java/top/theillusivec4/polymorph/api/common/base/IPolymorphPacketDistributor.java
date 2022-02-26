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

package top.theillusivec4.polymorph.api.common.base;

import java.util.SortedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IPolymorphPacketDistributor {

  void sendPlayerRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendPersistentRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendStackRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendRecipesListS2C(ServerPlayer pPlayer);

  void sendRecipesListS2C(ServerPlayer pPlayer, SortedSet<IRecipePair> pRecipesList);

  void sendRecipesListS2C(ServerPlayer pPlayer, SortedSet<IRecipePair> pRecipesList,
                          ResourceLocation pSelected);

  void sendHighlightRecipeS2C(ServerPlayer pPlayer, ResourceLocation pResourceLocation);

  void sendPlayerSyncS2C(ServerPlayer pPlayer, SortedSet<IRecipePair> pRecipesList,
                         ResourceLocation pSelected);

  void sendBlockEntitySyncS2C(BlockPos pBlockPos, ResourceLocation pSelected);
}
