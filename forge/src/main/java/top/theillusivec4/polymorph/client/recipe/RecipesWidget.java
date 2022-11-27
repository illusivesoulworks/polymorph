/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.client.recipe;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.SortedSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;
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

  public static void create(AbstractContainerScreen<?> pContainerScreen) {

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

      if (widget instanceof PersistentRecipesWidget &&
          Minecraft.getInstance().getConnection() != null) {
        PolymorphApi.common().getPacketDistributor().sendBlockEntityListener(true);
      }
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

    if (widget instanceof PersistentRecipesWidget &&
        Minecraft.getInstance().getConnection() != null) {
      PolymorphApi.common().getPacketDistributor().sendBlockEntityListener(false);
    }
    widget = null;
    lastScreen = null;
  }
}
