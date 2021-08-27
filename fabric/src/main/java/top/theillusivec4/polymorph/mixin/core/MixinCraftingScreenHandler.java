package top.theillusivec4.polymorph.mixin.core;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(CraftingScreenHandler.class)
public class MixinCraftingScreenHandler {

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/recipe/RecipeManager.getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "updateResult")
  private static <C extends Inventory, T extends Recipe<C>> Optional<T> polymorph$getFirstMatch(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world, int syncId,
      World unused, PlayerEntity player, CraftingInventory craftingInventory,
      CraftingResultInventory resultInventory) {
    return MixinHooks.getResult(recipeManager, type, inventory, world, player);
  }
}
