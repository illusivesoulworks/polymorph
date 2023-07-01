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

package com.illusivesoulworks.polymorph.platform;

import com.illusivesoulworks.polymorph.mixin.AccessorAbstractContainerScreen;
import com.illusivesoulworks.polymorph.platform.services.IClientPlatform;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class QuiltClientPlatform implements IClientPlatform {

  @Override
  public int getScreenTop(AbstractContainerScreen<?> screen) {
    return ((AccessorAbstractContainerScreen) screen).getTopPos();
  }

  @Override
  public int getScreenLeft(AbstractContainerScreen<?> screen) {
    return ((AccessorAbstractContainerScreen) screen).getLeftPos();
  }
}
