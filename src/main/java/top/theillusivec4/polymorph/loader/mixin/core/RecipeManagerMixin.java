package top.theillusivec4.polymorph.loader.mixin.core;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.loader.common.MixinHooks;

@Mixin(value = RecipeManager.class, priority = 1200)
public class RecipeManagerMixin {

  @Inject(at = @At("HEAD"), method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;", cancellable = true)
  private <C extends Inventory, T extends Recipe<C>> void _polymorph_getRecipe(
      RecipeType<T> recipeTypeIn, C inventoryIn, World worldIn,
      CallbackInfoReturnable<Optional<T>> cb) {
    MixinHooks.getSelectedRecipe(recipeTypeIn, inventoryIn, worldIn)
        .ifPresent(recipe -> cb.setReturnValue(Optional.of(recipe)));
  }
}
