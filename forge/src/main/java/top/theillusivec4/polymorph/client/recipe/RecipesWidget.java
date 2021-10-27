package top.theillusivec4.polymorph.client.recipe;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class RecipesWidget {

  private static IRecipesWidget widget = null;

  public static Optional<IRecipesWidget> get() {
    return Optional.ofNullable(widget);
  }

  public static void create(ContainerScreen<?> pContainerScreen) {
    Optional<IRecipesWidget> maybeWidget = PolymorphApi.client().getWidget(pContainerScreen);
    maybeWidget.ifPresent(newWidget -> widget = newWidget);

    if (widget == null) {
      PolymorphApi.client().findCraftingResultSlot(pContainerScreen)
          .ifPresent(slot -> widget = new PlayerRecipesWidget(pContainerScreen, slot));
    }

    if (widget != null) {
      widget.initChildWidgets();
    }
  }

  public static void clear() {
    widget = null;
  }
}
