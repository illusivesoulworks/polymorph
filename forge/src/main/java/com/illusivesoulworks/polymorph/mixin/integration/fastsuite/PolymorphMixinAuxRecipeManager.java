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

package com.illusivesoulworks.polymorph.mixin.integration.fastsuite;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import dev.shadowsoffire.fastsuite.AuxRecipeManager;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AuxRecipeManager.class)
public class PolymorphMixinAuxRecipeManager {

  @Inject(
      at = @At("HEAD"),
      method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
      cancellable = true)
  private <C extends Container, T extends Recipe<C>> void polymorph$getRecipe(
      RecipeType<T> recipeType, C container, Level level, CallbackInfoReturnable<Optional<T>> cb) {

    if (container instanceof BlockEntity blockEntity) {
      RecipeSelection.getBlockEntityRecipe(recipeType, container, level, blockEntity)
          .ifPresent(recipe -> cb.setReturnValue(Optional.of(recipe)));
    }
  }
}
