package top.theillusivec4.polymorph.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class ToggleSelectorButton extends ImageButton {

  private final ContainerScreen<?> containerScreen;
  private final int xOffset;
  private final int yOffset;

  public ToggleSelectorButton(ContainerScreen<?> containerScreen, int xIn, int yIn, int xOffset,
                            int yOffset, int widthIn, int heightIn, int xTexStartIn,
                            int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn,
                            Button.IPressable onPressIn) {
    super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn,
        256, 256, onPressIn);
    this.containerScreen = containerScreen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void renderWidget(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY,
                           float partialTicks) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.x = containerScreen.getGuiLeft() + this.xOffset;
    this.y = containerScreen.getGuiTop() + this.yOffset;
    super.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
  }
}
