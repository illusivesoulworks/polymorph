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

package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;

public class OpenSelectionButton extends ImageButton {

  private final ContainerScreen<?> containerScreen;
  private int xOffset;
  private int yOffset;

  public OpenSelectionButton(ContainerScreen<?> pContainerScreen, int pX, int pY, Button.IPressable pOnPress) {
    super(0,0, 16, 16, 0, 0, 17, AbstractRecipesWidget.WIDGETS, 256, 256, pOnPress);
    this.containerScreen = pContainerScreen;
    this.xOffset = pX;
    this.yOffset = pY;
  }

  public void setOffsets(int pX, int pY) {
    this.xOffset = pX;
    this.yOffset = pY;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void renderWidget(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY,
                           float pPartialTicks) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.x = containerScreen.getGuiLeft() + this.xOffset;
    this.y = containerScreen.getGuiTop() + this.yOffset;
    super.renderWidget(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
  }
}
