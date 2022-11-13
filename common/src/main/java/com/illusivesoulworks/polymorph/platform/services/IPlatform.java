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

package com.illusivesoulworks.polymorph.platform.services;

import com.illusivesoulworks.polymorph.api.common.base.IPolymorphPacketDistributor;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IStackRecipeData;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPlatform {

  PolymorphIntegrations.Loader getLoader();

  Path getGameDir();

  Path getConfigDir();

  boolean isModLoaded(String id);

  boolean isModFileLoaded(String id);

  boolean isShaped(Recipe<?> recipe);

  boolean isSameShape(Recipe<?> recipe1, Recipe<?> recipe2);

  Optional<? extends IPlayerRecipeData> getRecipeData(Player player);

  Optional<? extends IBlockEntityRecipeData> getRecipeData(BlockEntity blockEntity);

  Optional<? extends IStackRecipeData> getRecipeData(ItemStack stack);

  IPolymorphPacketDistributor getPacketDistributor();
}
