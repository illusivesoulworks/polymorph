/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.api.client.base;

import java.util.Optional;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public interface IPolymorphClient {

  Optional<IRecipesWidget> getWidget(AbstractContainerScreen<?> containerScreen);

  void registerWidget(IRecipesWidgetFactory factory);

  Optional<Slot> findCraftingResultSlot(AbstractContainerScreen<?> containerScreen);

  interface IRecipesWidgetFactory {

    IRecipesWidget createWidget(AbstractContainerScreen<?> containerScreen);
  }
}
