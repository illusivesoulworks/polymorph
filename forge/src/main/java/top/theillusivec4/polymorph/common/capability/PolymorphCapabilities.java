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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public class PolymorphCapabilities {

  public static final Capability<IPlayerRecipeData> PLAYER_RECIPE_DATA =
      CapabilityManager.get(new CapabilityToken<>() {
      });
  public static final Capability<IBlockEntityRecipeData> BLOCK_ENTITY_RECIPE_DATA =
      CapabilityManager.get(new CapabilityToken<>() {
      });
  public static final Capability<IStackRecipeData> STACK_RECIPE_DATA =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  public static final ResourceLocation PLAYER_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "player_recipe_data");
  public static final ResourceLocation BLOCK_ENTITY_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "block_entity_recipe_data");
  public static final ResourceLocation STACK_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "stack_recipe_data");

  public static LazyOptional<IPlayerRecipeData> getRecipeData(Player pPlayer) {
    return pPlayer.getCapability(PLAYER_RECIPE_DATA);
  }

  public static LazyOptional<IBlockEntityRecipeData> getRecipeData(BlockEntity pTileEntity) {
    return pTileEntity.getCapability(BLOCK_ENTITY_RECIPE_DATA);
  }

  public static LazyOptional<IStackRecipeData> getRecipeData(ItemStack pStack) {
    return pStack.getCapability(STACK_RECIPE_DATA);
  }
}
