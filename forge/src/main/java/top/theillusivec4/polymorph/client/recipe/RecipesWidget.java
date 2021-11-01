package top.theillusivec4.polymorph.client.recipe;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.SortedSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class RecipesWidget {

  private static IRecipesWidget widget = null;
  private static Screen lastScreen = null;
  private static Pair<SortedSet<IRecipePair>, ResourceLocation> pendingData = null;

  public static Optional<IRecipesWidget> get() {
    return Optional.ofNullable(widget);
  }

  public static void enqueueRecipesList(SortedSet<IRecipePair> pRecipesList,
                                        ResourceLocation pResourceLocation) {
    pendingData = new Pair<>(pRecipesList, pResourceLocation);
  }

  public static void create(ContainerScreen<?> pContainerScreen) {

    if (pContainerScreen == lastScreen && widget != null) {
      return;
    }
    Optional<IRecipesWidget> maybeWidget = PolymorphApi.client().getWidget(pContainerScreen);
    maybeWidget.ifPresent(newWidget -> widget = newWidget);

    if (widget == null) {
      PolymorphApi.client().findCraftingResultSlot(pContainerScreen)
          .ifPresent(slot -> widget = new PlayerRecipesWidget(pContainerScreen, slot));
    }

    if (widget != null) {
      widget.initChildWidgets();
      lastScreen = pContainerScreen;

      if (pendingData != null) {
        widget.setRecipesList(pendingData.getFirst(), pendingData.getSecond());
      }
    } else {
      lastScreen = null;
    }
    pendingData = null;
  }

  public static void clear() {
    widget = null;
    lastScreen = null;
  }
}
