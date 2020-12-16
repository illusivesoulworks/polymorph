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

package top.theillusivec4.polymorph.loader.impl;

import java.lang.reflect.Proxy;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import top.theillusivec4.polymorph.core.base.common.Accessor;
import top.theillusivec4.polymorph.loader.mixin.core.CraftingScreenHandlerAccessor;
import top.theillusivec4.polymorph.loader.mixin.core.IngredientAccessor;
import top.theillusivec4.polymorph.loader.mixin.core.PlayerScreenHandlerAccessor;

public class AccessorImpl implements Accessor {

  public CraftingInventory getCraftingInput(PlayerScreenHandler screenHandler) {
    return ((PlayerScreenHandlerAccessor) screenHandler).getCraftingInput();
  }

  public CraftingInventory getCraftingInput(CraftingScreenHandler screenHandler) {
    return ((CraftingScreenHandlerAccessor) screenHandler).getInput();
  }

  @Override
  public ItemStack[] getMatchingStacks(Ingredient ingredient) {
    IngredientAccessor accessor = (IngredientAccessor) Proxy
        .newProxyInstance(IngredientAccessor.class.getClassLoader(),
            new Class[] {IngredientAccessor.class}, (proxy, method, args) -> Ingredient.class
                .getMethod(method.getName(), method.getParameterTypes()).invoke(ingredient, args));
    return accessor.getMatchingStacks();
  }
}
