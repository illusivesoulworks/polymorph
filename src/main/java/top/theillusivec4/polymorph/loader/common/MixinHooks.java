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

package top.theillusivec4.polymorph.loader.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphComponent;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.client.selector.CraftingRecipeSelector;
import top.theillusivec4.polymorph.loader.util.EnvironmentExecutor;

public class MixinHooks {

  public static void updateSelector() {
    EnvironmentExecutor.runOnClient(() -> CraftingRecipeSelector::update);
  }

  @SuppressWarnings("unchecked")
  public static <C extends Inventory, T extends Recipe<C>> Optional<T> getSelectedRecipe(
      RecipeType<T> recipeType, C inventory, World world) {

    if (inventory instanceof BlockEntity) {
      BlockEntity te = (BlockEntity) inventory;
      Optional<PersistentSelector> cap = PolymorphComponent.SELECTOR.maybeGet(te);
      List<T> recipe = new ArrayList<>();
      cap.ifPresent(selector -> {
        ItemStack input = inventory.getStack(0);

        if (!input.isEmpty()) {
          Optional<T> maybeSelected = (Optional<T>) selector.getSelectedRecipe();
          maybeSelected.ifPresent(res -> {
            if (res.matches(inventory, world)) {
              recipe.add(res);
            } else {
              selector.fetchRecipe(world).ifPresent(res1 -> recipe.add((T) res1));
            }
          });

          if (!maybeSelected.isPresent()) {
            selector.fetchRecipe(world).ifPresent(res1 -> recipe.add((T) res1));
          }
        }
      });
      return recipe.isEmpty() ? Optional.empty() : Optional.of(recipe.get(0));
    }
    return Optional.empty();
  }
}
