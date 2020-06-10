package top.theillusivec4.polymorph;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.item.ItemStack;

public class RecipeSelectionGui extends AbstractGui implements IRenderable, IGuiEventListener {

  private List<RecipeSelectWidget> buttons;
  private RecipeSelectWidget hoveredButton;
  private boolean visible = false;

  public void setStacks(List<ItemStack> stacks) {
    buttons = new ArrayList<>();
    stacks.forEach(stack -> buttons.add(new RecipeSelectWidget(stack)));
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void renderTooltip(int p_193721_1_, int p_193721_2_) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      mc.currentScreen
          .renderTooltip(this.hoveredButton.getTooltipText(mc.currentScreen), p_193721_1_,
              p_193721_2_);
    }
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {

    if (this.isVisible()) {
      buttons.forEach(button -> {
        button.render(p_render_1_, p_render_2_, p_render_3_);
        this.hoveredButton = null;

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(p_render_1_, p_render_2_);
    }
  }
}
