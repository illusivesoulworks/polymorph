package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;

public class OpenSelectionButton extends ImageButton {

  private final ContainerScreen<?> containerScreen;
  private final int xOffset;
  private final int yOffset;

  public OpenSelectionButton(ContainerScreen<?> pContainerScreen, int pX, int pY, int pXOffset,
                             int pYOffset, Button.IPressable pOnPress) {
    super(pX, pY, 16, 16, 0, 0, 17, AbstractRecipesWidget.WIDGETS, 256, 256, pOnPress);
    this.containerScreen = pContainerScreen;
    this.xOffset = pXOffset;
    this.yOffset = pYOffset;
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
