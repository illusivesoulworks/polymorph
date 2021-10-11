package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;

public class ClientEventsListener {

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent evt) {

    if (evt.phase == TickEvent.Phase.END) {
      Minecraft mc = Minecraft.getInstance();
      RecipeControllerHub.getController().ifPresent(recipeController -> {
        if (mc.player == null || mc.player.openContainer == null || mc.currentScreen == null) {
          RecipeControllerHub.clear();
        } else {
          recipeController.tick();
        }
      });
    }
  }

  @SubscribeEvent
  public void initGui(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof ContainerScreen) {
      RecipeControllerHub.startController((ContainerScreen<?>) screen);
    }
  }

  @SubscribeEvent
  public void render(GuiScreenEvent.DrawScreenEvent.Post evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeControllerHub.getController().ifPresent(
          recipeController -> recipeController.render(evt.getMatrixStack(), evt.getMouseX(),
              evt.getMouseY(), evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void mouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeControllerHub.getController().ifPresent(recipeController -> {

        if (recipeController.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          evt.setCanceled(true);
        }
      });
    }
  }
}
