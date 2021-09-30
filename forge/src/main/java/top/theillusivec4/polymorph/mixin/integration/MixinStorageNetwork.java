package top.theillusivec4.polymorph.mixin.integration;

import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(ContainerNetwork.class)
public class MixinStorageNetwork {

  @Shadow
  protected PlayerEntity player;

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "findMatchingRecipe", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return MixinHooks.getRecipe(recipeManager, type, inventory, world, this.player);
  }

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "findMatchingRecipeClient", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipeClient(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return MixinHooks.getRecipe(recipeManager, type, inventory, world, this.player);
  }
}
