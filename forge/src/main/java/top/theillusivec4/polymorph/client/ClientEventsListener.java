package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

@SuppressWarnings("unused")
public class ClientEventsListener {

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent pEvent) {

    if (pEvent.phase == TickEvent.Phase.END) {
      Minecraft mc = Minecraft.getInstance();
      RecipesWidget.get().ifPresent(widget -> {
        if (mc.player == null || mc.player.openContainer == null || mc.currentScreen == null) {
          RecipesWidget.clear();
        } else if (widget instanceof ITickingRecipesWidget) {
          ((ITickingRecipesWidget) widget).tick();
        }
      });
    }
  }

  @SubscribeEvent
  public void initGui(GuiScreenEvent.InitGuiEvent.Post pEvent) {
    Screen screen = pEvent.getGui();

    if (screen instanceof ContainerScreen) {
      RecipesWidget.create((ContainerScreen<?>) screen);
    }
  }

  @SubscribeEvent
  public void render(GuiScreenEvent.DrawScreenEvent.Post pEvent) {

    if (pEvent.getGui() instanceof ContainerScreen) {
      RecipesWidget.get().ifPresent(
          recipeController -> recipeController.render(pEvent.getMatrixStack(), pEvent.getMouseX(),
              pEvent.getMouseY(), pEvent.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void mouseClick(GuiScreenEvent.MouseClickedEvent.Pre pEvent) {

    if (pEvent.getGui() instanceof ContainerScreen) {
      RecipesWidget.get().ifPresent(recipeController -> {
        if (recipeController.mouseClicked(pEvent.getMouseX(), pEvent.getMouseY(),
            pEvent.getButton())) {
          pEvent.setCanceled(true);
        }
      });
    }
  }
}
