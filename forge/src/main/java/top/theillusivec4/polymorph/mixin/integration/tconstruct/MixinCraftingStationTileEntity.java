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

package top.theillusivec4.polymorph.mixin.integration.tconstruct;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.RetexturedTableTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.crafting.CraftingInventoryWrapper;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.tconstruct.TinkersConstructModule;

@SuppressWarnings("unused")
@Mixin(CraftingStationTileEntity.class)
public abstract class MixinCraftingStationTileEntity extends RetexturedTableTileEntity {

  @Shadow(remap = false)
  private ICraftingRecipe lastRecipe;

  @Shadow(remap = false)
  @Final
  private CraftingInventoryWrapper craftingInventory;

  public MixinCraftingStationTileEntity(TileEntityType<?> type,
                                        String name, int size) {
    super(type, name, size);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "calcResult")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      @Nullable PlayerEntity player) {
    return RecipeSelection.getTileEntityRecipe(type, inventory, world, this);
  }

  @Inject(
      at = @At("HEAD"),
      method = "calcResult",
      remap = false)
  private void polymorph$calcResult(@Nullable PlayerEntity player,
                                    CallbackInfoReturnable<ItemStack> cir) {
    TinkersConstructModule.calcResult(this, player, this.lastRecipe, this.craftingInventory);
  }
}
