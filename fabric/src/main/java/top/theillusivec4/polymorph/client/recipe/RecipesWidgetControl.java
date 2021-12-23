/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.client.recipe;

import java.util.Optional;
import java.util.SortedSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.RecipesWidget;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class RecipesWidgetControl {

  private static RecipesWidget widget = null;
  private static Screen lastScreen = null;
  private static Pair<SortedSet<RecipePair>, Identifier> pendingData = null;

  public static Optional<RecipesWidget> get() {
    return Optional.ofNullable(widget);
  }

  public static void enqueueRecipesList(SortedSet<RecipePair> pRecipesList,
                                        Identifier pIdentifier) {
    pendingData = new Pair<>(pRecipesList, pIdentifier);
  }

  public static void create(HandledScreen<?> pContainerScreen) {

    if (pContainerScreen == lastScreen && widget != null) {
      return;
    }
    Optional<RecipesWidget> maybeWidget = PolymorphApi.client().getWidget(pContainerScreen);
    maybeWidget.ifPresent(newWidget -> widget = newWidget);

    if (widget == null) {
      PolymorphApi.client().findCraftingResultSlot(pContainerScreen)
          .ifPresent(slot -> widget = new PlayerRecipesWidget(pContainerScreen, slot));
    }

    if (widget != null) {
      widget.initChildWidgets();
      lastScreen = pContainerScreen;

      if (pendingData != null) {
        widget.setRecipesList(pendingData.getLeft(), pendingData.getRight());
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
