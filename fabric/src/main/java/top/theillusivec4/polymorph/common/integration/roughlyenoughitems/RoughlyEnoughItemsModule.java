package top.theillusivec4.polymorph.common.integration.roughlyenoughitems;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

@Environment(EnvType.CLIENT)
public class RoughlyEnoughItemsModule implements REIPluginV0 {

  @Override
  public void registerBounds(DisplayHelper displayHelper) {
    BaseBoundsHandler.getInstance()
        .registerExclusionZones(HandledScreen.class, new PolymorphExclusionZones());
  }

  @Override
  public Identifier getPluginIdentifier() {
    return new Identifier(PolymorphMod.MOD_ID + ":rei_plugin");
  }

  public static void selectRecipe(RecipeDisplay pRecipeDisplay) {
    pRecipeDisplay.getRecipeLocation().ifPresent(recipe -> {
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeIdentifier(recipe);
      ClientPlayNetworking.send(PolymorphPackets.PLAYER_SELECT, buf);
    });
  }

  private static class PolymorphExclusionZones implements Supplier<List<Rectangle>> {

    @Override
    public List<Rectangle> get() {
      Rectangle rect = new Rectangle();
      RecipesWidgetControl.get().ifPresent(widget -> {
        Screen screen = MinecraftClient.getInstance().currentScreen;

        if (screen instanceof HandledScreen<?> && widget.getSelectionWidget().isActive()) {
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
