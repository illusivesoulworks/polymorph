package top.theillusivec4.polymorph.common.integration.roughlyenoughitems;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.AbstractRecipeController;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

@Environment(EnvType.CLIENT)
public class PolymorphReiPlugin implements REIPluginV0 {

  @Override
  public void registerBounds(DisplayHelper displayHelper) {
    BaseBoundsHandler.getInstance()
        .registerExclusionZones(HandledScreen.class, new PolymorphExclusionZones());
  }

  @Override
  public Identifier getPluginIdentifier() {
    return new Identifier(PolymorphMod.MOD_ID + ":rei_plugin");
  }

  private static class PolymorphExclusionZones implements Supplier<List<Rectangle>> {

    @Override
    public List<Rectangle> get() {
      Rectangle rect = new Rectangle();
      RecipeControllerHub.getController().ifPresent(recipeController -> {
        Screen screen = MinecraftClient.getInstance().currentScreen;

        if (screen instanceof HandledScreen<?> && recipeController.getSelectorWidget().isActive()) {
          int x = ((AccessorHandledScreen) screen).getX() + recipeController.getXPos();
          int y = ((AccessorHandledScreen) screen).getY() + recipeController.getYPos();
          int size = recipeController.getSelectorWidget().getOutputWidgets().size();
          int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

          if (size % 2 == 0) {
            xOffset += 13;
          }
          rect.x = x + AbstractRecipeController.SELECTOR_X_OFFSET + xOffset;
          rect.y = y + AbstractRecipeController.SELECTOR_Y_OFFSET;
          rect.height = 25;
          rect.width = 25 * size;
        }
      });
      return Collections.singletonList(rect);
    }
  }
}
