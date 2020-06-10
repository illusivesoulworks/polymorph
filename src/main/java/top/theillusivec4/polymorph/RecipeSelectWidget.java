package top.theillusivec4.polymorph;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeSelectWidget extends Widget {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");
  private ItemStack output;
  private float time;
  private float animationTime;

  public RecipeSelectWidget(ItemStack output) {
    super(0, 0, 25, 25, "");
    this.output = output;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
    Minecraft minecraft = Minecraft.getInstance();
    minecraft.getTextureManager().bindTexture(SWITCH);
    int i = 16;
    int j = 0;
    boolean flag = this.animationTime > 0.0F;

    if (flag) {
      float f = 1.0F + 0.1F * (float) Math.sin((this.animationTime / 15.0F * (float) Math.PI));
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float) (this.x + 8), (float) (this.y + 12), 0.0F);
      RenderSystem.scalef(f, f, 1.0F);
      RenderSystem.translatef((float) (-(this.x + 8)), (float) (-(this.y + 12)), 0.0F);
      this.animationTime -= p_renderButton_3_;
    }
    this.blit(this.x, this.y, i, j, this.width, this.height);
    int k = 4;
    minecraft.getItemRenderer().renderItemAndEffectIntoGUI(output, this.x + k, this.y + k);

    if (flag) {
      RenderSystem.popMatrix();
    }
  }

  public List<String> getTooltipText(Screen screen) {
    return screen.getTooltipFromItem(output);
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected boolean isValidClickButton(int p_isValidClickButton_1_) {
    return p_isValidClickButton_1_ == 0 || p_isValidClickButton_1_ == 1;
  }
}