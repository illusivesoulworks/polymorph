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

package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.PatternTermContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class PatternTermRecipesWidget extends PlayerRecipesWidget {

  private final PatternTermContainer container;

  public PatternTermRecipesWidget(ContainerScreen<?> containerScreen,
                                  PatternTermContainer container, Slot outputSlot) {
    super(containerScreen, outputSlot);
    this.container = container;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    super.selectRecipe(pResourceLocation);
    this.container.getPlayerInventory().player.getEntityWorld().getRecipeManager()
        .getRecipe(pResourceLocation).ifPresent(recipe -> {
          ((AccessorPatternTermContainer) this.container).setCurrentRecipe((ICraftingRecipe) recipe);
          ((AccessorPatternTermContainer) this.container).callGetAndUpdateOutput();
        });
  }
}
