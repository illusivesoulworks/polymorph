package top.theillusivec4.polymorph.mixin.integration;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPatternFactory;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

@Mixin(CraftingPatternFactory.class)
public class MixinRefinedStoragePattern {

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "create", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getPatternRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world, World unused,
      ICraftingPatternContainer container, ItemStack stack) {
    return RefinedStorageHooks.getPatternRecipe(stack, type, inventory, world);
  }
}
