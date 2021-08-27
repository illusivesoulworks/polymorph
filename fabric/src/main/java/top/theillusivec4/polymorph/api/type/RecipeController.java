/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.api.type;

import java.util.Set;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface RecipeController<I extends Inventory, R extends Recipe<I>> {

  void selectRecipe(R recipe);

  void highlightRecipe(String recipe);

  void setRecipes(Set<Identifier> recipes, World world, Identifier selected);

  default void tick() {
    // NO-OP
  }

  void render(MatrixStack matrixStack, int mouseX, int mouseY, float renderPartialTicks);

  boolean mouseClicked(double mouseX, double mouseY, int button);

  I getInventory();

  Slot getOutputSlot();

  default int getXPos() {
    return getOutputSlot().x;
  }

  default int getYPos() {
    return getOutputSlot().y - 22;
  }

  default boolean isActive() { return true; }
}
