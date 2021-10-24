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
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

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

  public SelectionWidget(int x, int y, int xOffset, int yOffset,
                         Consumer<ResourceLocation> onSelect, ContainerScreen<?> screen) {
    this.setPosition(x, y);
    this.onSelect = onSelect;
    this.containerScreen = screen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.updateButtonPositions();
  }

  public void highlightButton(ResourceLocation recipe) {
    this.outputWidgets.forEach(
        widget -> widget.setHighlighted(widget.getResourceLocation().equals(recipe)));
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

  public void setRecipeList(Set<IRecipeData> pRecipeData) {
    this.outputWidgets.clear();
    pRecipeData.forEach(data -> this.outputWidgets.add(new OutputWidget(data)));
    this.updateButtonPositions();
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }

  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      this.renderTooltip(this.hoveredButton.getOutput(), matrixStack,
          this.hoveredButton.getTooltipText(mc.currentScreen), mouseX, mouseY);
    }
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float delta) {

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
        button.render(matrixStack, mouseX, mouseY, delta);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.isActive()) {

      for (OutputWidget widget : this.outputWidgets) {

        if (widget.mouseClicked(mouseX, mouseY, button)) {
          onSelect.accept(widget.getResourceLocation());
          return true;
        }
      }
    }
    return false;
  }

  public void renderTooltip(ItemStack stack, MatrixStack p_243308_1_,
                            List<ITextComponent> p_243308_2_, int p_243308_3_, int p_243308_4_) {
    drawHoveringText(stack, p_243308_1_, p_243308_2_, p_243308_3_, p_243308_4_,
        this.containerScreen.width, this.containerScreen.height, -1,
        GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BORDER_COLOR_START,
        GuiUtils.DEFAULT_BORDER_COLOR_END, this.containerScreen.getMinecraft().fontRenderer);
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
          font.drawEntityText(LanguageMap.getInstance().func_241870_a(line), (float) tooltipX,
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
