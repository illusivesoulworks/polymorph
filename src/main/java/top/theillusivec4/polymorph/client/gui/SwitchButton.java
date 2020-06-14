package top.theillusivec4.polymorph.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class SwitchButton extends ImageButton {

  public SwitchButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn,
      int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn) {
    super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn,
        256, 256, onPressIn);
  }

  @Override
  public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
    RenderSystem.pushMatrix();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
    RenderSystem.popMatrix();
  }
}
