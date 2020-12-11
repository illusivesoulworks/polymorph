package top.theillusivec4.polymorph.mixin;

import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.MixinHooks;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

  @SuppressWarnings("unchecked")
  @Inject(at = @At("HEAD"), method = "getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;", cancellable = true)
  private <C extends IInventory, T extends IRecipe<C>> void _polymorph_getRecipe(
      IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn,
      CallbackInfoReturnable<Optional<T>> cb) {
    T recipe = (T) MixinHooks.getSelectedRecipe(inventoryIn);

    if (recipe != null) {
      cb.setReturnValue(Optional.of(recipe));
    }
  }
}
