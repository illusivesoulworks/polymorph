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

package com.illusivesoulworks.polymorph.platform;

import com.illusivesoulworks.polymorph.api.common.base.IPolymorphPacketDistributor;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IPlayerRecipeData;
import com.illusivesoulworks.polymorph.api.common.capability.IStackRecipeData;
import com.illusivesoulworks.polymorph.common.PolymorphFabricPacketDistributor;
import com.illusivesoulworks.polymorph.common.components.PolymorphFabricComponents;
import com.illusivesoulworks.polymorph.platform.services.IPlatform;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FabricPlatform implements IPlatform {

  private static final IPolymorphPacketDistributor PACKET_DISTRIBUTOR =
      new PolymorphFabricPacketDistributor();

  @Override
  public Path getGameDir() {
    return FabricLoader.getInstance().getGameDir();
  }

  @Override
  public Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isModFileLoaded(String id) {
    return isModLoaded(id);
  }

  @Override
  public boolean isShaped(Recipe<?> recipe) {
    return recipe instanceof ShapedRecipe;
  }

  @Override
  public boolean isSameShape(Recipe<?> recipe1, Recipe<?> recipe2) {

    if (isShaped(recipe1) && isShaped(recipe2)) {
      ShapedRecipe shaped = (ShapedRecipe) recipe1;
      ShapedRecipe otherShaped = (ShapedRecipe) recipe2;
      return shaped.getHeight() == otherShaped.getHeight() &&
          shaped.getWidth() == otherShaped.getWidth();
    }
    return true;
  }

  @Override
  public Optional<? extends IPlayerRecipeData> getRecipeData(Player player) {
    return PolymorphFabricComponents.PLAYER_RECIPE_DATA.maybeGet(player);
  }

  @Override
  public Optional<? extends IBlockEntityRecipeData> getRecipeData(BlockEntity blockEntity) {
    return PolymorphFabricComponents.BLOCK_ENTITY_RECIPE_DATA.maybeGet(blockEntity);
  }

  @Override
  public Optional<? extends IStackRecipeData> getRecipeData(ItemStack stack) {
    return PolymorphFabricComponents.STACK_RECIPE_DATA.maybeGet(stack);
  }

  @Override
  public IPolymorphPacketDistributor getPacketDistributor() {
    return PACKET_DISTRIBUTOR;
  }
}
