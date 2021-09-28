package top.theillusivec4.polymorph.mixin.integration;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbench.MixinHooks;

@Mixin(MixinHooks.class)
public class MixinFastBench {

  @Redirect(at = @At(value = "INVOKE", target = "tfar/fastbench/MixinHooks.findRecipe(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Lnet/minecraft/recipe/Recipe;"), method = "slotChangedCraftingGrid")
  private static Recipe<CraftingInventory> polymorph$findRecipe(CraftingInventory inv, World world,
                                                                World unused1, PlayerEntity player,
                                                                CraftingInventory unused2,
                                                                CraftingResultInventory result) {
    return top.theillusivec4.polymorph.mixin.util.MixinHooks.getResult(world.getRecipeManager(),
        RecipeType.CRAFTING, inv, world, player).orElse(null);
  }
}
