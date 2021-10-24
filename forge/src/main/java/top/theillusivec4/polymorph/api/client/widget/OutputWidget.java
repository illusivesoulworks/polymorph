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
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

public class OutputWidget extends Widget {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;
  private boolean highlighted = false;

  public OutputWidget(IRecipeData pRecipeData) {
    super(0, 0, 25, 25, StringTextComponent.EMPTY);
    this.output = pRecipeData.getOutput();
    this.resourceLocation = pRecipeData.getResourceLocation();
  }

  @Override
  public void renderWidget(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY,
                           float partialTicks) {
    Minecraft minecraft = Minecraft.getInstance();
    minecraft.getTextureManager().bindTexture(AbstractRecipesWidget.WIDGETS);
    int j = 0;

    if (this.x + 25 > mouseX && this.x <= mouseX &&
        this.y + 25 > mouseY && this.y <= mouseY) {
      j += 25;
    }
    blit(matrixStack, this.x, this.y, 600, this.highlighted ? 41 : 16, j, this.width, this.height,
        256, 256);
    int k = 4;
    float zLevel = minecraft.getItemRenderer().zLevel;
    minecraft.getItemRenderer().zLevel = 600.0F;
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

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean isHighlighted() {
    return highlighted;
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
  }

  public List<ITextComponent> getTooltipText(Screen screen) {
    return screen.getTooltipFromItem(this.getOutput());
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected boolean isValidClickButton(int button) {
    return button == 0 || button == 1;
  }
}
