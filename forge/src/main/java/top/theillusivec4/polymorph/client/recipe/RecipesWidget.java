package top.theillusivec4.polymorph.client.recipe;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.CraftingRecipesWidget;

public class RecipesWidget {

  private static IRecipesWidget widget = null;

  public static Optional<IRecipesWidget> get() {
    return Optional.ofNullable(widget);
  }

  public static void create(ContainerScreen<?> containerScreen) {
    Optional<IRecipesWidget> maybeWidget = PolymorphApi.client().getWidget(containerScreen);
    maybeWidget.ifPresent(newWidget -> widget = newWidget);

    if (widget == null) {
      PolymorphApi.client().getCraftingPair(containerScreen).ifPresent(data ->
          widget = new CraftingRecipesWidget(containerScreen, data.getSecond(), data.getFirst()));
    }

    if (widget != null) {
      widget.initChildWidgets();
    }
  }

  public static void clear() {
    widget = null;
  }
}
