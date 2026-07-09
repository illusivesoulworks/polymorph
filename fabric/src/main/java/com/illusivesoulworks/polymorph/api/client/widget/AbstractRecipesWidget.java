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

package com.illusivesoulworks.polymorph.api.client.widget;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.client.base.IRecipesWidget;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractRecipesWidget implements IRecipesWidget {

  public static final ResourceLocation WIDGETS =
      new ResourceLocation(PolymorphApi.MOD_ID, "textures/gui/widgets.png");
  public static final int BUTTON_X_OFFSET = 0;
  public static final int BUTTON_Y_OFFSET = -22;
  public static final int WIDGET_X_OFFSET = -4;
  public static final int WIDGET_Y_OFFSET = -26;

  protected final AbstractContainerScreen<?> containerScreen;
  protected final int xOffset;
  protected final int yOffset;

  protected SelectionWidget selectionWidget;
  protected OpenSelectionButton openButton;

  public AbstractRecipesWidget(AbstractContainerScreen<?> containerScreen, int xOffset,
                               int yOffset) {
    this.containerScreen = containerScreen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  public AbstractRecipesWidget(AbstractContainerScreen<?> containerScreen) {
    this(containerScreen, WIDGET_X_OFFSET, WIDGET_Y_OFFSET);
  }

  @Override
  public void initChildWidgets() {
    int x = Services.CLIENT_PLATFORM.getScreenLeft(this.containerScreen) + this.getXPos();
    int y = Services.CLIENT_PLATFORM.getScreenTop(this.containerScreen) + this.getYPos();
    this.selectionWidget =
        new SelectionWidget(x + this.xOffset, y + this.yOffset, this.getXPos() + this.xOffset,
            this.getYPos() + this.yOffset, this::selectRecipe, this.containerScreen);
    this.openButton = new OpenSelectionButton(this.containerScreen, this.getXPos(), this.getYPos(),
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
  public abstract void selectRecipe(ResourceLocation resourceLocation);

  @Override
  public SelectionWidget getSelectionWidget() {
    return selectionWidget;
  }

  @Override
  public void highlightRecipe(ResourceLocation resourceLocation) {
    this.selectionWidget.highlightButton(resourceLocation);
  }

  @Override
  public void setRecipesList(Set<IRecipePair> recipesList, ResourceLocation selected) {
    SortedSet<IRecipePair> sorted = new TreeSet<>(recipesList);
    this.selectionWidget.setRecipeList(sorted);
    this.openButton.visible = recipesList.size() > 1;

    if (selected != null) {
      this.highlightRecipe(selected);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPartialTicks) {
    this.selectionWidget.render(guiGraphics, mouseX, mouseY, renderPartialTicks);
    this.openButton.render(guiGraphics, mouseX, mouseY, renderPartialTicks);
  }

  @Override
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
