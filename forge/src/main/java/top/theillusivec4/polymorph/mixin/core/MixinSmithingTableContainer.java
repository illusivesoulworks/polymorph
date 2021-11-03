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

package top.theillusivec4.polymorph.mixin.core;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SmithingTableContainer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@SuppressWarnings("unused")
@Mixin(SmithingTableContainer.class)
public abstract class MixinSmithingTableContainer extends AbstractRepairContainer {

  @Unique
  private List<SmithingRecipe> recipes;

  @Shadow
  private SmithingRecipe selectedRecipe;

  public MixinSmithingTableContainer(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_,
                                     PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
    super(p_i231587_1_, p_i231587_2_, p_i231587_3_, p_i231587_4_);
  }

  @ModifyVariable(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipesFor(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/List;"),
      method = "createResult")
  private List<SmithingRecipe> polymorph$getRecipes(List<SmithingRecipe> recipes) {
    this.recipes = recipes;

    if (this.player instanceof ServerPlayerEntity && recipes.isEmpty()) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) this.player);
    }
    return recipes;
  }

  @Inject(
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "java/util/List.get(I)Ljava/lang/Object;",
          shift = At.Shift.BY,
          by = 3),
      method = "createResult")
  private void polymorph$updateRepairOutput(CallbackInfo ci) {
    this.selectedRecipe =
        RecipeSelection.getPlayerRecipe(IRecipeType.SMITHING, this.inputSlots,
            this.player.level, this.player, this.recipes).orElse(null);
  }
}
