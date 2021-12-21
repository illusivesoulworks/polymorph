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

package top.theillusivec4.polymorph.mixin.integration.appliedenergistics2;

import appeng.api.storage.ITerminalHost;
import appeng.container.me.items.ItemTerminalContainer;
import appeng.container.me.items.PatternTermContainer;
import java.util.Optional;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@SuppressWarnings("unused")
@Mixin(PatternTermContainer.class)
public abstract class MixinPatternTermContainer extends ItemTerminalContainer {

  public MixinPatternTermContainer(int id, PlayerInventory ip, ITerminalHost monitorable) {
    super(id, ip, monitorable);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/recipe/RecipeManager.getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "getAndUpdateOutput")
  private <C extends Inventory, T extends Recipe<C>> Optional<T> polymorph$getFirstMatch(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world,
        this.getPlayerInventory().player);
  }
}
