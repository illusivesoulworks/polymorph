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

package top.theillusivec4.polymorph.api.client.widget;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.client.base.RecipesWidget;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

public abstract class AbstractRecipesWidget implements RecipesWidget {

  public static final Identifier WIDGETS = new Identifier(PolymorphMod.MOD_ID,
      "textures/gui/widgets.png");
  public static final int BUTTON_X_OFFSET = 0;
  public static final int BUTTON_Y_OFFSET = -22;
  public static final int WIDGET_X_OFFSET = -4;
  public static final int WIDGET_Y_OFFSET = -26;

  protected final HandledScreen<?> handledScreen;
  protected final int xOffset;
  protected final int yOffset;

  protected SelectionWidget selectionWidget;
  protected OpenSelectionButton openButton;

  public AbstractRecipesWidget(HandledScreen<?> pScreen, int pXOffset, int pYOffset) {
    this.handledScreen = pScreen;
    this.xOffset = pXOffset;
    this.yOffset = pYOffset;
  }

  public AbstractRecipesWidget(HandledScreen<?> pScreen) {
    this(pScreen, WIDGET_X_OFFSET, WIDGET_Y_OFFSET);
  }

  @Override
  public void initChildWidgets() {
    int x = ((AccessorHandledScreen) this.handledScreen).getX() + this.getXPos();
    int y = ((AccessorHandledScreen) this.handledScreen).getY() + this.getYPos();
    this.selectionWidget =
        new SelectionWidget(x + this.xOffset, y + this.yOffset, this.getXPos() + this.xOffset,
            this.getYPos() + this.yOffset, this::selectRecipe, this.handledScreen);
    this.openButton =
        new OpenSelectionButton(this.handledScreen, this.getXPos(), this.getYPos(),
            clickWidget -> this.selectionWidget.setActive(!this.selectionWidget.isActive()));
    this.openButton.visible = this.selectionWidget.getOutputWidgets().size() > 1;
  }

  protected void resetWidgetOffsets() {
    int x = this.getXPos();
    int y = this.getYPos();
    this.selectionWidget.setOffsets(x + this.xOffset, y + this.yOffset);
    this.openButton.setOffsets(x, y);
  }

  @Override
  public abstract void selectRecipe(Identifier pIdentifier);


  @Override
  public SelectionWidget getSelectionWidget() {
    return selectionWidget;
  }

  @Override
  public void highlightRecipe(Identifier pRecipe) {
    this.selectionWidget.highlightButton(pRecipe);
  }

  @Override
  public void setRecipesList(Set<RecipePair> pRecipesList, Identifier pSelected) {
    SortedSet<RecipePair> sorted = new TreeSet<>(pRecipesList);
    this.selectionWidget.setRecipeList(sorted);
    this.openButton.visible = pRecipesList.size() > 1;

    if (pSelected != null) {
      this.highlightRecipe(pSelected);
    }
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.selectionWidget.render(matrixStack, mouseX, mouseY, partialTicks);
    this.openButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.openButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.selectionWidget.mouseClicked(mouseX, mouseY, button)) {
      this.selectionWidget.setActive(false);
      return true;
    } else if (this.selectionWidget.isActive()) {

      if (!this.openButton.mouseClicked(mouseX, mouseY, button)) {
        this.selectionWidget.setActive(false);
      }
      return true;
    }
    return false;
  }

  @Override
  public int getXPos() {
    return this.getOutputSlot().x + BUTTON_X_OFFSET;
  }

  @Override
  public int getYPos() {
    return this.getOutputSlot().y + BUTTON_Y_OFFSET;
  }
}
