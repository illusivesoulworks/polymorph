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

package com.illusivesoulworks.polymorph.common.integration.fastfurnace;

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.reflect.FieldUtils;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(BlockEntity blockEntity, Recipe<?> recipe) {

    if (recipe instanceof AbstractCookingRecipe &&
        blockEntity instanceof AbstractFurnaceBlockEntity) {
      try {
        FieldUtils.writeField(blockEntity, "curRecipe", recipe, true);
      } catch (IllegalAccessException | IllegalArgumentException e) {
        PolymorphIntegrations.disable("fastfurnace");
        PolymorphConstants.LOG.error(
            "Polymorph encountered an error with its fastfurnace integration.");
        PolymorphConstants.LOG.error("The integration module for fastfurnace will be disabled.");
        PolymorphConstants.LOG.error(
            "Please report this bug to Polymorph only, do not report this to fastfurnace.");
        e.printStackTrace();
      }
    }
    return false;
  }
}
