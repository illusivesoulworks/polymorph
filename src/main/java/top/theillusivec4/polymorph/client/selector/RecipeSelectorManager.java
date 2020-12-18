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

package top.theillusivec4.polymorph.client.selector;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;

public class RecipeSelectorManager {

  private static IRecipeSelector<? extends IInventory, ? extends IRecipe<?>> selector = null;
  private static ResourceLocation preferredRecipe = null;

  public static Optional<IRecipeSelector<? extends IInventory, ? extends IRecipe<?>>> getSelector() {
    return Optional.ofNullable(selector);
  }

  public static boolean tryCreate(ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().getProvider(screen.getContainer()).map(provider -> {
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

  public static void setPreferredRecipe(ResourceLocation id) {
    preferredRecipe = id;
  }

  public static Optional<ResourceLocation> getPreferredRecipe() {
    return Optional.ofNullable(preferredRecipe);
  }
}
