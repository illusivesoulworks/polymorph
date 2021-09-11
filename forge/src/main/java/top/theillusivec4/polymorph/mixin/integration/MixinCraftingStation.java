package top.theillusivec4.polymorph.mixin.integration;

import com.tfar.craftingstation.CraftingStationContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(CraftingStationContainer.class)
public class MixinCraftingStation {

  @Inject(at = @At(value = "RETURN"), method = "findRecipe", cancellable = true, remap = false)
  private static void polymorph$getRecipe(CraftingInventory inv, World world, PlayerEntity player,
                                          CallbackInfoReturnable<IRecipe<CraftingInventory>> cir) {
    MixinHooks.getRecipe(world.getRecipeManager(), IRecipeType.CRAFTING, inv, world, player)
        .ifPresent(cir::setReturnValue);
  }
}
