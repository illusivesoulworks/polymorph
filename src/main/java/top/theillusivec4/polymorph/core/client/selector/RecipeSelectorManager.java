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

package top.theillusivec4.polymorph.core.client.selector;

import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.RecipeSelector;

public class RecipeSelectorManager {

  private static RecipeSelector<? extends Inventory, ? extends Recipe<?>> selector = null;
  private static Identifier preferredRecipe = null;

  public static Optional<RecipeSelector<? extends Inventory, ? extends Recipe<?>>> getSelector() {
    return Optional.ofNullable(selector);
  }

  public static boolean tryCreate(HandledScreen<?> screen) {
    return PolymorphApi.getInstance().getProvider(screen.getScreenHandler()).map(provider -> {
      if (provider.isActive()) {
        selector = provider.createSelector(screen);
        return true;
      }
      return false;
    }).orElse(false);
  }

  public static void clear() {
    selector = null;
  }

  public static void setPreferredRecipe(Identifier id) {
    preferredRecipe = id;
  }

  public static Optional<Identifier> getPreferredRecipe() {
    return Optional.ofNullable(preferredRecipe);
  }
}
