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

package top.theillusivec4.polymorph.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class RecipeSelectorGui<I extends IInventory, R extends IRecipe<I>> extends AbstractGui
    implements IRenderable, IGuiEventListener {

  private final Consumer<R> select;
  private final I inventory;
  private final Screen screen;
  private final List<RecipeOutputWidget<I, R>> buttons = new ArrayList<>();

  private RecipeOutputWidget<I, R> hoveredButton;
  private boolean visible = false;
  private int x;
  private int y;
  private List<R> recipes = new ArrayList<>();

  public RecipeSelectorGui(int x, int y, I inventory, Consumer<R> select, Screen screen) {
    this.setPosition(x, y);
    this.select = select;
    this.inventory = inventory;
    this.screen = screen;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.updateButtonPositions();
  }

  public void highlightButton(String recipe) {
    this.buttons.forEach(button -> {
      String id = button.recipe.getId().toString();
      button.highlighted = id.equals(recipe);
    });
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

  public List<RecipeOutputWidget<I, R>> getButtons() {
    return buttons;
  }

  public void setRecipes(List<R> recipes) {
    this.recipes = recipes;
    this.buttons.clear();
    recipes
        .forEach(recipe -> this.buttons.add(new RecipeOutputWidget<>(inventory, recipe)));
    this.updateButtonPositions();
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void renderTooltip(MatrixStack matrixStack, int p_193721_1_, int p_193721_2_) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      this.renderTooltip(this.hoveredButton.getOutput(), matrixStack,
          this.hoveredButton.getTooltipText(mc.currentScreen),
          p_193721_1_, p_193721_2_);
    }
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int p_render_1_, int p_render_2_,
                     float p_render_3_) {

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

      for (RecipeOutputWidget<I, R> button : this.buttons) {

        if (button.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
          select.accept(button.recipe);
          return true;
        }
      }
    }
    return false;
  }

  public void renderTooltip(ItemStack stack, MatrixStack p_243308_1_,
                            List<ITextComponent> p_243308_2_,
                            int p_243308_3_, int p_243308_4_) {
    drawHoveringText(stack, p_243308_1_, p_243308_2_, p_243308_3_, p_243308_4_, this.screen.width,
        this.screen.height, -1, GuiUtils.DEFAULT_BACKGROUND_COLOR,
        GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END,
        this.screen.getMinecraft().fontRenderer);
  }

  @SuppressWarnings("deprecation")
  public static void drawHoveringText(@Nonnull final ItemStack stack, MatrixStack mStack,
                                      List<? extends ITextProperties> textLines, int mouseX,
                                      int mouseY, int screenWidth, int screenHeight,
                                      int maxTextWidth, int backgroundColor, int borderColorStart,
                                      int borderColorEnd, FontRenderer font) {
    if (!textLines.isEmpty()) {
      RenderTooltipEvent.Pre event =
          new RenderTooltipEvent.Pre(stack, textLines, mStack, mouseX, mouseY, screenWidth,
              screenHeight, maxTextWidth, font);
      if (MinecraftForge.EVENT_BUS.post(event)) {
        return;
      }
      mouseX = event.getX();
      mouseY = event.getY();
      screenWidth = event.getScreenWidth();
      screenHeight = event.getScreenHeight();
      maxTextWidth = event.getMaxWidth();
      font = event.getFontRenderer();
      RenderSystem.disableRescaleNormal();
      RenderSystem.disableDepthTest();
      int tooltipTextWidth = 0;

      for (ITextProperties textLine : textLines) {
        int textLineWidth = font.getStringPropertyWidth(textLine);

        if (textLineWidth > tooltipTextWidth) {
          tooltipTextWidth = textLineWidth;
        }
      }
      boolean needsWrap = false;
      int titleLinesCount = 1;
      int tooltipX = mouseX + 12;

      if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
        tooltipX = mouseX - 16 - tooltipTextWidth;
        if (tooltipX < 4) {
          if (mouseX > screenWidth / 2) {
            tooltipTextWidth = mouseX - 12 - 8;
          } else {
            tooltipTextWidth = screenWidth - 16 - mouseX;
          }
          needsWrap = true;
        }
      }

      if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
        tooltipTextWidth = maxTextWidth;
        needsWrap = true;
      }

      if (needsWrap) {
        int wrappedTooltipWidth = 0;
        List<ITextProperties> wrappedTextLines = new ArrayList<>();
        for (int i = 0; i < textLines.size(); i++) {
          ITextProperties textLine = textLines.get(i);
          List<ITextProperties> wrappedLine =
              font.getCharacterManager().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);
          if (i == 0) {
            titleLinesCount = wrappedLine.size();
          }

          for (ITextProperties line : wrappedLine) {
            int lineWidth = font.getStringPropertyWidth(line);
            if (lineWidth > wrappedTooltipWidth) {
              wrappedTooltipWidth = lineWidth;
            }
            wrappedTextLines.add(line);
          }
        }
        tooltipTextWidth = wrappedTooltipWidth;
        textLines = wrappedTextLines;

        if (mouseX > screenWidth / 2) {
          tooltipX = mouseX - 16 - tooltipTextWidth;
        } else {
          tooltipX = mouseX + 12;
        }
      }

      int tooltipY = mouseY - 12;
      int tooltipHeight = 8;

      if (textLines.size() > 1) {
        tooltipHeight += (textLines.size() - 1) * 10;
        if (textLines.size() > titleLinesCount) {
          tooltipHeight += 2;
        }
      }

      if (tooltipY < 4) {
        tooltipY = 4;
      } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
        tooltipY = screenHeight - tooltipHeight - 4;
      }

      final int zLevel = 900;
      RenderTooltipEvent.Color colorEvent =
          new RenderTooltipEvent.Color(stack, textLines, mStack, tooltipX, tooltipY, font,
              backgroundColor, borderColorStart, borderColorEnd);
      MinecraftForge.EVENT_BUS.post(colorEvent);
      backgroundColor = colorEvent.getBackground();
      borderColorStart = colorEvent.getBorderStart();
      borderColorEnd = colorEvent.getBorderEnd();

      mStack.push();
      Matrix4f mat = mStack.getLast().getMatrix();
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3, backgroundColor, backgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor,
          backgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3,
          tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor,
          backgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
          tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart,
          borderColorEnd);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3 + 1, borderColorStart, borderColorStart);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd,
          borderColorEnd);

      MinecraftForge.EVENT_BUS.post(
          new RenderTooltipEvent.PostBackground(stack, textLines, mStack, tooltipX, tooltipY, font,
              tooltipTextWidth, tooltipHeight));
      IRenderTypeBuffer.Impl renderType =
          IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
      mStack.translate(0.0D, 0.0D, zLevel);
      int tooltipTop = tooltipY;

      for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
        ITextProperties line = textLines.get(lineNumber);
        if (line != null) {
          font.func_238416_a_(LanguageMap.getInstance().func_241870_a(line), (float) tooltipX,
              (float) tooltipY, -1, true, mat, renderType, false, 0, 15728880);
        }

        if (lineNumber + 1 == titleLinesCount) {
          tooltipY += 2;
        }
        tooltipY += 10;
      }
      renderType.finish();
      mStack.pop();
      MinecraftForge.EVENT_BUS.post(
          new RenderTooltipEvent.PostText(stack, textLines, mStack, tooltipX, tooltipTop, font,
              tooltipTextWidth, tooltipHeight));
      RenderSystem.enableDepthTest();
      RenderSystem.enableRescaleNormal();
    }
  }
}
