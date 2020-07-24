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

package top.theillusivec4.polymorph.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;

public class RecipeSelectionGui extends DrawableHelper implements Drawable, Element {

  private final Consumer<Recipe<CraftingInventory>> recipeSelector;
  private final CraftingInventory craftingInventory;

  private List<RecipeSelectWidget> buttons = new ArrayList<>();
  private RecipeSelectWidget hoveredButton;
  private boolean visible = false;
  private int x;
  private int y;
  private List<CraftingRecipe> recipes = new ArrayList<>();

  public RecipeSelectionGui(int x, int y, CraftingInventory craftingInventory,
      Consumer<Recipe<CraftingInventory>> recipeSelector) {
    this.setPosition(x, y);
    this.recipeSelector = recipeSelector;
    this.craftingInventory = craftingInventory;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.updateButtonPositions();
  }

  private void updateButtonPositions() {
    int size = recipes.size();
    int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

    if (size % 2 == 0) {
      xOffset += 13;
    }
    int[] pos = {this.x + xOffset, this.y};
    this.buttons.forEach(button -> {
      button.setPosition(pos[0], pos[1]);
      pos[0] += 25;
    });
  }

  public List<RecipeSelectWidget> getButtons() {
    return buttons;
  }

  public void setRecipes(List<CraftingRecipe> recipes) {
    this.recipes = recipes;
    this.buttons.clear();
    recipes.forEach(recipe -> this.buttons.add(new RecipeSelectWidget(craftingInventory, recipe)));
    this.updateButtonPositions();
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void renderTooltip(MatrixStack matrixStack, int p_193721_1_, int p_193721_2_) {
    MinecraftClient mc = MinecraftClient.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      mc.currentScreen
          .renderTooltip(matrixStack, this.hoveredButton.getTooltipText(mc.currentScreen),
              p_193721_1_, p_193721_2_);
    }
  }

  @Override
  public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {

    if (this.isVisible()) {
      this.hoveredButton = null;
      buttons.forEach(button -> {
        button.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }
  }

  @Override
  public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_,
      int p_mouseClicked_5_) {

    if (this.isVisible()) {

      for (RecipeSelectWidget button : this.buttons) {

        if (button.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
          recipeSelector.accept(button.recipe);
          return true;
        }
      }
    }
    return false;
  }
}
