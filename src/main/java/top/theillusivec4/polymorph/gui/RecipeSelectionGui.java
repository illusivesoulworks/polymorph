package top.theillusivec4.polymorph.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketSetOutput;
import top.theillusivec4.polymorph.util.ClientCraftingManager;

public class RecipeSelectionGui extends AbstractGui implements IRenderable, IGuiEventListener {

  private final ClientCraftingManager craftingManager;
  private List<RecipeSelectWidget> buttons = new ArrayList<>();
  private RecipeSelectWidget hoveredButton;
  private boolean visible = false;

  public RecipeSelectionGui(ClientCraftingManager craftingManager) {
    this.craftingManager = craftingManager;
  }

  public void setRecipes(List<ICraftingRecipe> recipes) {
    this.buttons.clear();
    this.craftingManager.getCurrentCraftingMatrix().ifPresent(craftingInventory -> recipes
        .forEach(recipe -> this.buttons.add(new RecipeSelectWidget(craftingInventory, recipe))));
    int[] pos = {0, 0};
    this.buttons.forEach(button -> {
      button.setPosition(pos[0], 0);
      pos[0] += 25;
    });
    this.craftingManager.getSwitchButton().visible = recipes.size() > 1;
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

  @Override
  public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_,
      int p_mouseClicked_5_) {

    if (this.isVisible()) {

      for (RecipeSelectWidget button : this.buttons) {

        if (button.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
          craftingManager.setLastSelectedRecipe(button.recipe);
          ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

          if (playerEntity != null) {
            Container container = playerEntity.openContainer;

            if (container instanceof WorkbenchContainer) {
              WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;

              craftingManager.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {
                ItemStack stack = button.recipe.getCraftingResult(craftingInventory);
                workbenchContainer.getSlot(workbenchContainer.getOutputSlot())
                    .putStack(stack.copy());
                NetworkHandler.INSTANCE
                    .send(PacketDistributor.SERVER.noArg(), new CPacketSetOutput(stack));
              });
            }
          }
          return true;
        }
      }
    }
    return false;
  }
}
