package top.theillusivec4.polymorph.mixin.integration;

import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.recipes.RecipeTransferManager;
import mezz.jei.transfer.RecipeTransferUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.integrations.jei.JeiMixinHooks;

@Mixin(RecipeTransferUtil.class)
public class JeiMixin {

  @Inject(at = @At("HEAD"), method = "transferRecipe(Lmezz/jei/recipes/RecipeTransferManager;Lnet/minecraft/inventory/container/Container;Lmezz/jei/gui/recipes/RecipeLayout;Lnet/minecraft/entity/player/PlayerEntity;Z)Z", remap = false)
  private static void _polymorph_transferRecipe(RecipeTransferManager recipeTransferManager,
                                                Container container, RecipeLayout<?> recipeLayout,
                                                PlayerEntity player, boolean maxTransfer,
                                                CallbackInfoReturnable<IRecipeTransferError> cb) {
    JeiMixinHooks.setRecipe(recipeLayout.getRecipe());
  }
}
