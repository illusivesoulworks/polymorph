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

package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(BlockEntity tileEntity, Recipe<?> recipe) {

    if (recipe instanceof AbstractCookingRecipe &&
        tileEntity instanceof AbstractFurnaceBlockEntity) {
      try {
        FieldUtils.writeField(tileEntity, "curRecipe", recipe, true);
      } catch (IllegalAccessException e) {
        PolymorphMod.LOGGER.error("Cannot access curRecipe in FastFurnace!");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        PolymorphMod.LOGGER.error("Cannot find curRecipe in FastFurnace!");
        e.printStackTrace();
      }
    }
    return false;
  }
}
