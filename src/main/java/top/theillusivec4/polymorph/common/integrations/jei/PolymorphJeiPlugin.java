/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common.integrations.jei;

import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.Polymorph;

@JeiPlugin
public class PolymorphJeiPlugin implements IModPlugin {

  @Nonnull
  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(Polymorph.MODID, "jei");
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    JeiModule.ingredientListOverlay = jeiRuntime.getIngredientListOverlay();
    JeiModule.bookmarkOverlay = jeiRuntime.getBookmarkOverlay();
  }
}
