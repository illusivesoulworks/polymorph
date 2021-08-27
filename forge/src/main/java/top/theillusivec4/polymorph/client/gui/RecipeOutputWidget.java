package top.theillusivec4.polymorph.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class RecipeOutputWidget<T extends IInventory, R extends IRecipe<T>> extends Widget {

  private static final ResourceLocation TOGGLE = new ResourceLocation(PolymorphMod.MOD_ID,
      "textures/gui/toggle.png");
  private final ItemStack output;
  private final R recipe;
  public boolean highlighted = false;

  public RecipeOutputWidget(R recipe, ItemStack output) {
    super(0, 0, 25, 25, StringTextComponent.EMPTY);
    this.output = output;
    this.recipe = recipe;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public void renderWidget(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY,
                           float partialTicks) {
    Minecraft minecraft = Minecraft.getInstance();
    minecraft.getTextureManager().bindTexture(TOGGLE);
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

  public R getRecipe() {
    return this.recipe;
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
