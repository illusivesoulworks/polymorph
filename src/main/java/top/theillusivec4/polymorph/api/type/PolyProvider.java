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

package top.theillusivec4.polymorph.api.type;

import java.util.List;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public interface PolyProvider<I extends Inventory, R extends Recipe<I>> {

  default boolean isActive() {
    return true;
  }

  ScreenHandler getScreenHandler();

  I getInventory();

  Slot getOutputSlot();

  List<? extends R> getRecipes(World world, RecipeManager recipeManager);

  RecipeSelector<I, R> createSelector(HandledScreen<?> screen);

  default int getXPos() {
    return getOutputSlot().x;
  }

  default int getYPos() {
    return getOutputSlot().y - 22;
  }
}