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

package top.theillusivec4.polymorph.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.client.selector.CraftingRecipeSelector;

public class MixinHooks {

  public static void sendCraftingUpdate() {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CraftingRecipeSelector::update);
  }

  @SuppressWarnings("unchecked")
  public static <C extends IInventory, T extends IRecipe<C>> Optional<T> getSelectedRecipe(
      IRecipeType<T> recipeType, C inventory, World world) {

    if (inventory instanceof TileEntity) {
      TileEntity te = (TileEntity) inventory;
      LazyOptional<IPersistentSelector> cap =
          te.getCapability(PolymorphCapability.PERSISTENT_SELECTOR);
      List<T> recipe = new ArrayList<>();
      cap.ifPresent(selector -> {
        ItemStack input = inventory.getStackInSlot(0);

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
