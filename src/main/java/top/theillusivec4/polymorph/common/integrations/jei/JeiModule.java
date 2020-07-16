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

import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IIngredientListOverlay;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.client.RecipeConflictManager;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class JeiModule extends CompatibilityModule {

  static IIngredientListOverlay ingredientListOverlay;
  static IBookmarkOverlay bookmarkOverlay;

  @Override
  public void clientSetup() {
    MinecraftForge.EVENT_BUS.register(new PolymorphJeiPlugin());
  }

  @SubscribeEvent
  public void guiClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    ItemStack chosenStack = ItemStack.EMPTY;

    if (ingredientListOverlay != null) {
      Object ingredient = ingredientListOverlay.getIngredientUnderMouse();

      if (ingredient instanceof ItemStack) {
        chosenStack = (ItemStack) ingredient;
      }
    }

    if (chosenStack.isEmpty() && bookmarkOverlay != null) {
      Object ingredient = bookmarkOverlay.getIngredientUnderMouse();

      if (ingredient instanceof ItemStack) {
        chosenStack = (ItemStack) ingredient;
      }
    }

    if (!chosenStack.isEmpty()) {
      RecipeConflictManager.setPreferredStack(chosenStack);
    }
  }
}
