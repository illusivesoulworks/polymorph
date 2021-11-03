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
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class OutputWidget extends Widget {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;
  private boolean highlighted = false;

  public OutputWidget(IRecipePair pRecipeData) {
    super(0, 0, 25, 25, StringTextComponent.EMPTY);
    this.output = pRecipeData.getOutput();
    this.resourceLocation = pRecipeData.getResourceLocation();
  }

  @Override
  public void renderWidget(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY,
                           float pPartialTicks) {
    Minecraft minecraft = Minecraft.getInstance();
    minecraft.getTextureManager().bindTexture(AbstractRecipesWidget.WIDGETS);
    int j = 0;

    if (this.x + 25 > pMouseX && this.x <= pMouseX &&
        this.y + 25 > pMouseY && this.y <= pMouseY) {
      j += 25;
    }
    blit(pMatrixStack, this.x, this.y, 600, this.highlighted ? 41 : 16, j, this.width, this.height,
        256, 256);
    int k = 4;
    float zLevel = minecraft.getItemRenderer().zLevel;
    minecraft.getItemRenderer().zLevel = 700.0F;
    minecraft.getItemRenderer()
        .renderItemAndEffectIntoGUI(this.getOutput(), this.x + k, this.y + k);
    minecraft.getItemRenderer().zLevel = zLevel;
  }

  public ItemStack getOutput() {
    return this.output;
  }

  public ResourceLocation getResourceLocation() {
    return this.resourceLocation;
  }

  public void setPosition(int pX, int pY) {
    this.x = pX;
    this.y = pY;
  }

  public boolean isHighlighted() {
    return highlighted;
  }

  public void setHighlighted(boolean pHighlighted) {
    this.highlighted = pHighlighted;
  }

  public List<ITextComponent> getTooltipText(Screen pScreen) {
    return pScreen.getTooltipFromItem(this.getOutput());
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected boolean isValidClickButton(int pButton) {
    return pButton == 0 || pButton == 1;
  }
}
