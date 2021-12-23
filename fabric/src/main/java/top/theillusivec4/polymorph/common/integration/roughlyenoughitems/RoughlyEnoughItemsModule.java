package top.theillusivec4.polymorph.common.integration.roughlyenoughitems;

import java.util.Collection;
import java.util.Collections;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import me.shedaniel.rei.api.common.display.Display;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.PacketByteBuf;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

@Environment(EnvType.CLIENT)
public class RoughlyEnoughItemsModule implements REIClientPlugin {

  @Override
  public void registerExclusionZones(ExclusionZones zones) {
    zones.register(HandledScreen.class, new PolymorphExclusionZones());
  }

  public static void selectRecipe(Display pRecipeDisplay) {
    pRecipeDisplay.getDisplayLocation().ifPresent(recipe -> {
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeIdentifier(recipe);
      ClientPlayNetworking.send(PolymorphPackets.PLAYER_SELECT, buf);
    });
  }

  private static class PolymorphExclusionZones implements ExclusionZonesProvider<HandledScreen<?>> {

    @Override
    public Collection<Rectangle> provide(HandledScreen<?> screen) {
      Rectangle rect = new Rectangle();
      RecipesWidgetControl.get().ifPresent(widget -> {

        if (screen != null && widget.getSelectionWidget().isActive()) {
          int x = ((AccessorHandledScreen) screen).getX() + widget.getXPos();
          int y = ((AccessorHandledScreen) screen).getY() + widget.getYPos();
          int size = widget.getSelectionWidget().getOutputWidgets().size();
          int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

          if (size % 2 == 0) {
            xOffset += 13;
          }
          rect.x = x + AbstractRecipesWidget.WIDGET_X_OFFSET + xOffset;
          rect.y = y + AbstractRecipesWidget.WIDGET_Y_OFFSET;
          rect.height = 25;
          rect.width = 25 * size;
        }
      });
      return Collections.singletonList(rect);
    }
  }
}
