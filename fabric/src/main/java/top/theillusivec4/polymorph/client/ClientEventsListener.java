package top.theillusivec4.polymorph.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import top.theillusivec4.polymorph.api.client.base.TickingRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;

public class ClientEventsListener {

  public static void setup() {
    ClientTickEvents.END_CLIENT_TICK.register(ClientEventsListener::endClientTick);
  }

  public static void initScreen(Screen screen) {

    if (screen instanceof HandledScreen) {
      RecipesWidgetControl.create((HandledScreen<?>) screen);
    }
  }

  public static void renderScreen(Screen screen, MatrixStack matrices, int mouseX, int mouseY,
                                  float delta) {

    if (screen instanceof HandledScreen) {
      RecipesWidgetControl.get()
          .ifPresent(widget -> widget.render(matrices, mouseX, mouseY, delta));
    }
  }

  public static boolean clickScreen(Screen screen, double mouseX, double mouseY, int button) {

    if (screen instanceof HandledScreen) {
      return RecipesWidgetControl.get().map(widget -> widget.mouseClicked(mouseX, mouseY, button))
          .orElse(false);
    }
    return false;
  }

  private static void endClientTick(final MinecraftClient pClient) {
    RecipesWidgetControl.get().ifPresent(widget -> {
      if (pClient.player == null || pClient.player.currentScreenHandler == null ||
          pClient.currentScreen == null) {
        RecipesWidgetControl.clear();
      } else if (widget instanceof TickingRecipesWidget) {
        ((TickingRecipesWidget) widget).tick();
      }
    });
  }
}
