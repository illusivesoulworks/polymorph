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

package top.theillusivec4.polymorph.client.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;

public class PolymorphClient implements IPolymorphClient {

  private static final IPolymorphClient INSTANCE = new PolymorphClient();

  public static IPolymorphClient get() {
    return INSTANCE;
  }

  private final List<IRecipesWidgetFactory> widgetFactories = new LinkedList<>();

  public Optional<IRecipesWidget> getWidget(AbstractContainerScreen<?> pContainerScreen) {

    for (IRecipesWidgetFactory factory : this.widgetFactories) {
      IRecipesWidget widget = factory.createWidget(pContainerScreen);

      if (widget != null) {
        return Optional.of(widget);
      }
    }
    return Optional.empty();
  }


  @Override
  public void registerWidget(IRecipesWidgetFactory pFactory) {
    this.widgetFactories.add(pFactory);
  }

  @Override
  public Optional<Slot> findCraftingResultSlot(AbstractContainerScreen<?> pContainerScreen) {
    AbstractContainerMenu container = pContainerScreen.getMenu();

    for (Slot slot : container.slots) {

      if (slot.container instanceof ResultContainer) {
        return Optional.of(slot);
      }
    }
    return Optional.empty();
  }
}
