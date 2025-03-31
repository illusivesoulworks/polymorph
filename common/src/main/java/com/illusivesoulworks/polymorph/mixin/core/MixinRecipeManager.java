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

package com.illusivesoulworks.polymorph.mixin.core;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipeContext;
import com.illusivesoulworks.polymorph.api.common.capability.IRecipeData;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = RecipeManager.class, priority = 900)
public class MixinRecipeManager implements IRecipeContext {

  @Unique
  Object polymorph$context;

  @Inject(
      at = @At("HEAD"),
      method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;",
      cancellable = true)
  private <I extends RecipeInput, T extends Recipe<I>> void polymorph$getRecipe(
      RecipeType<T> recipeType, I inventory, Level level, @Nullable RecipeHolder<T> recipeHolder,
      CallbackInfoReturnable<Optional<RecipeHolder<T>>> cb) {

    if (this.polymorph$getContext() instanceof BlockEntity blockEntity) {
      IRecipeData<?> data = PolymorphApi.getInstance().getBlockEntityRecipeData(blockEntity);

      if (data != null) {
        PolymorphApi.getInstance().getRecipeManager()
            .getBlockEntityRecipe(recipeType, inventory, level, blockEntity)
            .ifPresent(recipe -> cb.setReturnValue(Optional.of(recipe)));
      }
    }
  }

  @Nullable
  @Override
  public Object polymorph$getContext() {
    return this.polymorph$context;
  }

  @Override
  public void polymorph$setContext(Object context) {
    this.polymorph$context = context;
  }
}
