package top.theillusivec4.polymorph;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScreenEventHandler {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");

  public static ImageButton switchButton;
  public static RecipeSelectionGui recipeSelectionGui;

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      recipeSelectionGui = new RecipeSelectionGui();
      int x = screen.width / 2;
      int y = screen.height / 2;
      ImageButton button = new ImageButton(x + 36, y - 72, 16, 16, 0, 0, 17, SWITCH,
          clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
      evt.addWidget(button);
      button.visible = ClientCraftingManager.getResultsList().size() > 1;
      switchButton = button;
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {

      if (recipeSelectionGui != null) {
        recipeSelectionGui.render(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks());
      }
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {

      if (recipeSelectionGui != null) {

        if (recipeSelectionGui.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          recipeSelectionGui.setVisible(false);
          evt.setCanceled(true);
        }
      }
    }
  }
}
