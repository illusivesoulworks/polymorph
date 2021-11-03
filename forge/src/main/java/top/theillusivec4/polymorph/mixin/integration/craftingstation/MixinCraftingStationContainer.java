package top.theillusivec4.polymorph.mixin.integration.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@SuppressWarnings("unused")
@Mixin(CraftingStationContainer.class)
public class MixinCraftingStationContainer {

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "com/tfar/craftingstation/CraftingStationContainer.findRecipe(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/crafting/IRecipe;"),
      method = "slotChangedCraftingGrid",
      remap = false)
  private IRecipe<CraftingInventory> polymorph$findRecipe(CraftingInventory pInv, World pWorld,
                                                          PlayerEntity pPlayer) {
    return RecipeSelection.getPlayerRecipe(IRecipeType.CRAFTING, pInv, pWorld, pPlayer)
        .orElse(null);
  }
}
