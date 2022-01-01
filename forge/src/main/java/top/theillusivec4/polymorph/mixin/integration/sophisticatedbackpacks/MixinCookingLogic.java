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

package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.cooking.CookingLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;

@SuppressWarnings("unused")
@Mixin(CookingLogic.class)
public abstract class MixinCookingLogic {

  @Shadow(remap = false)
  @Final
  private ItemStack upgrade;

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/p3pp3rf1y/sophisticatedbackpacks/util/RecipeHelper.getCookingRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/crafting/IRecipeType;)Ljava/util/Optional;"),
      method = "getCookingRecipe",
      remap = false)
  private Optional<? extends AbstractCookingRecipe> polymorph$getCookingRecipe(ItemStack pStack,
                                                                               IRecipeType<? extends AbstractCookingRecipe> pRecipeType) {
    return SophisticatedBackpacksModule.getCookingRecipe(pStack, pRecipeType, this.upgrade);
  }
}
