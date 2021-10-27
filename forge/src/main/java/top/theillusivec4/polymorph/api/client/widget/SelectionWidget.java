package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class SelectionWidget extends AbstractGui
    implements IRenderable, IGuiEventListener {

  private final Consumer<ResourceLocation> onSelect;
  private final ContainerScreen<?> containerScreen;
  private final List<OutputWidget> outputWidgets = new ArrayList<>();
  private final int xOffset;
  private final int yOffset;

  private OutputWidget hoveredButton;
  private boolean active = false;
  private int x;
  private int y;
  private int lastX;
  private int lastY;

  public SelectionWidget(int pX, int pY, int pXOffset, int pYOffset,
                         Consumer<ResourceLocation> pOnSelect,
                         ContainerScreen<?> pContainerScreen) {
    this.setPosition(pX, pY);
    this.onSelect = pOnSelect;
    this.containerScreen = pContainerScreen;
    this.xOffset = pXOffset;
    this.yOffset = pYOffset;
  }

  public void setPosition(int pX, int pY) {
    this.x = pX;
    this.y = pY;
    this.updateButtonPositions();
  }

  public void highlightButton(ResourceLocation pResourceLocation) {
    this.outputWidgets.forEach(
        widget -> widget.setHighlighted(widget.getResourceLocation().equals(pResourceLocation)));
  }

  private void updateButtonPositions() {
    int size = this.outputWidgets.size();
    int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

    if (size % 2 == 0) {
      xOffset += 13;
    }
    int[] pos = {this.x + xOffset, this.y};
    this.outputWidgets.forEach(widget -> {
      widget.setPosition(pos[0], pos[1]);
      pos[0] += 25;
    });
  }

  public List<OutputWidget> getOutputWidgets() {
    return outputWidgets;
  }

  public void setRecipeList(Set<IRecipePair> pRecipeList) {
    this.outputWidgets.clear();
    pRecipeList.forEach(data -> this.outputWidgets.add(new OutputWidget(data)));
    this.updateButtonPositions();
  }

  public void setActive(boolean pActive) {
    this.active = pActive;
  }

  public boolean isActive() {
    return this.active;
  }

  public void renderTooltip(MatrixStack pMatrixStack, int pMouseX, int pMouseY) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      this.renderTooltip(this.hoveredButton.getOutput(), pMatrixStack,
          this.hoveredButton.getTooltipText(mc.currentScreen), pMouseX, pMouseY);
    }
  }

  @Override
  public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY,
                     float pPartialTicks) {

    if (this.isActive()) {
      int x = containerScreen.getGuiLeft() + this.xOffset;
      int y = containerScreen.getGuiTop() + this.yOffset;

      if (this.lastX != x || this.lastY != y) {
        this.setPosition(x, y);
        this.lastX = x;
        this.lastY = y;
      }
      this.hoveredButton = null;
      this.outputWidgets.forEach(button -> {
        button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(pMatrixStack, pMouseX, pMouseY);
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (this.isActive()) {

      for (OutputWidget widget : this.outputWidgets) {

        if (widget.mouseClicked(pMouseX, pMouseY, pButton)) {
          onSelect.accept(widget.getResourceLocation());
          return true;
        }
      }
    }
    return false;
  }

  public void renderTooltip(ItemStack pStack, MatrixStack pMatrixStack,
                            List<ITextComponent> pText, int pMouseX, int pMouseY) {
    drawHoveringText(pStack, pMatrixStack, pText, pMouseX, pMouseY, this.containerScreen.width,
        this.containerScreen.height, -1, GuiUtils.DEFAULT_BACKGROUND_COLOR,
        GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END,
        this.containerScreen.getMinecraft().fontRenderer);
  }

  @SuppressWarnings("deprecation")
  public static void drawHoveringText(@Nonnull final ItemStack pStack, MatrixStack pMatrixStack,
                                      List<? extends ITextProperties> pText, int pMouseX,
                                      int pMouseY, int pScreenWidth, int pScreenHeight,
                                      int pMaxTextWidth, int pBackgroundColor,
                                      int pBorderColorStart, int pBorderColorEnd,
                                      FontRenderer pFontRenderer) {

    if (!pText.isEmpty()) {
      RenderTooltipEvent.Pre event =
          new RenderTooltipEvent.Pre(pStack, pText, pMatrixStack, pMouseX, pMouseY, pScreenWidth,
              pScreenHeight, pMaxTextWidth, pFontRenderer);

      if (MinecraftForge.EVENT_BUS.post(event)) {
        return;
      }
      pMouseX = event.getX();
      pMouseY = event.getY();
      pScreenWidth = event.getScreenWidth();
      pScreenHeight = event.getScreenHeight();
      pMaxTextWidth = event.getMaxWidth();
      pFontRenderer = event.getFontRenderer();
      RenderSystem.disableRescaleNormal();
      RenderSystem.disableDepthTest();
      int tooltipTextWidth = 0;

      for (ITextProperties textLine : pText) {
        int textLineWidth = pFontRenderer.getStringPropertyWidth(textLine);

        if (textLineWidth > tooltipTextWidth) {
          tooltipTextWidth = textLineWidth;
        }
      }
      boolean needsWrap = false;
      int titleLinesCount = 1;
      int tooltipX = pMouseX + 12;

      if (tooltipX + tooltipTextWidth + 4 > pScreenWidth) {
        tooltipX = pMouseX - 16 - tooltipTextWidth;

        if (tooltipX < 4) {

          if (pMouseX > pScreenWidth / 2) {
            tooltipTextWidth = pMouseX - 12 - 8;
          } else {
            tooltipTextWidth = pScreenWidth - 16 - pMouseX;
          }
          needsWrap = true;
        }
      }

      if (pMaxTextWidth > 0 && tooltipTextWidth > pMaxTextWidth) {
        tooltipTextWidth = pMaxTextWidth;
        needsWrap = true;
      }

      if (needsWrap) {
        int wrappedTooltipWidth = 0;
        List<ITextProperties> wrappedTextLines = new ArrayList<>();

        for (int i = 0; i < pText.size(); i++) {
          ITextProperties textLine = pText.get(i);
          List<ITextProperties> wrappedLine =
              pFontRenderer.getCharacterManager()
                  .func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);

          if (i == 0) {
            titleLinesCount = wrappedLine.size();
          }

          for (ITextProperties line : wrappedLine) {
            int lineWidth = pFontRenderer.getStringPropertyWidth(line);

            if (lineWidth > wrappedTooltipWidth) {
              wrappedTooltipWidth = lineWidth;
            }
            wrappedTextLines.add(line);
          }
        }
        tooltipTextWidth = wrappedTooltipWidth;
        pText = wrappedTextLines;

        if (pMouseX > pScreenWidth / 2) {
          tooltipX = pMouseX - 16 - tooltipTextWidth;
        } else {
          tooltipX = pMouseX + 12;
        }
      }
      int tooltipY = pMouseY - 12;
      int tooltipHeight = 8;

      if (pText.size() > 1) {
        tooltipHeight += (pText.size() - 1) * 10;

        if (pText.size() > titleLinesCount) {
          tooltipHeight += 2;
        }
      }

      if (tooltipY < 4) {
        tooltipY = 4;
      } else if (tooltipY + tooltipHeight + 4 > pScreenHeight) {
        tooltipY = pScreenHeight - tooltipHeight - 4;
      }
      final int zLevel = 900;
      RenderTooltipEvent.Color colorEvent =
          new RenderTooltipEvent.Color(pStack, pText, pMatrixStack, tooltipX, tooltipY,
              pFontRenderer,
              pBackgroundColor, pBorderColorStart, pBorderColorEnd);
      MinecraftForge.EVENT_BUS.post(colorEvent);
      pBackgroundColor = colorEvent.getBackground();
      pBorderColorStart = colorEvent.getBorderStart();
      pBorderColorEnd = colorEvent.getBorderEnd();

      pMatrixStack.push();
      Matrix4f mat = pMatrixStack.getLast().getMatrix();
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3, pBackgroundColor, pBackgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, pBackgroundColor,
          pBackgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY + tooltipHeight + 3, pBackgroundColor, pBackgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3,
          tooltipY + tooltipHeight + 3, pBackgroundColor, pBackgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, pBackgroundColor,
          pBackgroundColor);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
          tooltipY + tooltipHeight + 3 - 1, pBorderColorStart, pBorderColorEnd);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, pBorderColorStart,
          pBorderColorEnd);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3 + 1, pBorderColorStart, pBorderColorStart);
      GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, pBorderColorEnd,
          pBorderColorEnd);

      MinecraftForge.EVENT_BUS.post(
          new RenderTooltipEvent.PostBackground(pStack, pText, pMatrixStack, tooltipX, tooltipY,
              pFontRenderer,
              tooltipTextWidth, tooltipHeight));
      IRenderTypeBuffer.Impl renderType =
          IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
      pMatrixStack.translate(0.0D, 0.0D, zLevel);
      int tooltipTop = tooltipY;

      for (int lineNumber = 0; lineNumber < pText.size(); ++lineNumber) {
        ITextProperties line = pText.get(lineNumber);

        if (line != null) {
          pFontRenderer.drawEntityText(LanguageMap.getInstance().func_241870_a(line),
              (float) tooltipX,
              (float) tooltipY, -1, true, mat, renderType, false, 0, 15728880);
        }

        if (lineNumber + 1 == titleLinesCount) {
          tooltipY += 2;
        }
        tooltipY += 10;
      }
      renderType.finish();
      pMatrixStack.pop();
      MinecraftForge.EVENT_BUS.post(
          new RenderTooltipEvent.PostText(pStack, pText, pMatrixStack, tooltipX, tooltipTop,
              pFontRenderer,
              tooltipTextWidth, tooltipHeight));
      RenderSystem.enableDepthTest();
      RenderSystem.enableRescaleNormal();
    }
  }
}
