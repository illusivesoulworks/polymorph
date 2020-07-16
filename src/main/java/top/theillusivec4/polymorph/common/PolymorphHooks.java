/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common;

import java.lang.reflect.Field;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.client.RecipeConflictManager;

public class PolymorphHooks {

  private static final Field IS_SIMPLE = ObfuscationReflectionHelper
      .findField(ShapelessRecipe.class, "isSimple");

  public static void onInventoryChanged(CraftResultInventory inventory) {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> RecipeConflictManager::updateManager);
  }

  public static void packIngredients(ShapelessRecipe shapelessRecipe) {
    boolean isSimple = true;

    try {
      isSimple = IS_SIMPLE.getBoolean(shapelessRecipe);
    } catch (IllegalAccessException e) {
      Polymorph.LOGGER
          .error("Error getting isSimple from shapeless recipe " + shapelessRecipe.getId());
    }

    if (isSimple) {

      for (Ingredient ingredient : shapelessRecipe.getIngredients()) {
        ingredient.getValidItemStacksPacked();
      }
    }
  }
}
