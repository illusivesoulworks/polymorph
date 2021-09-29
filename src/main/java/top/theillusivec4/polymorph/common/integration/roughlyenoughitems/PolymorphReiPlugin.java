package top.theillusivec4.polymorph.common.integration.roughlyenoughitems;

import java.util.Collection;
import java.util.Collections;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import top.theillusivec4.polymorph.client.recipe.AbstractRecipeController;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

public class PolymorphReiPlugin implements REIClientPlugin {

  @Override
  public void registerExclusionZones(ExclusionZones zones) {
    zones.register(HandledScreen.class, new PolymorphExclusionZones());
  }

  private static class PolymorphExclusionZones implements ExclusionZonesProvider<HandledScreen<?>> {

    @Override
    public Collection<Rectangle> provide(HandledScreen<?> screen) {
      Rectangle rect = new Rectangle();
      RecipeControllerHub.getController().ifPresent(recipeController -> {

        if (recipeController.getSelectorWidget().isActive()) {
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
