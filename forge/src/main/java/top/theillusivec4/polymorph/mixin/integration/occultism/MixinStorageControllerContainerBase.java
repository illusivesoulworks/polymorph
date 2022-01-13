package top.theillusivec4.polymorph.mixin.integration.occultism;

import com.github.klikli_dev.occultism.common.container.storage.StorageControllerContainerBase;
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
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(StorageControllerContainerBase.class)
public class MixinStorageControllerContainerBase {

  @Shadow
  public PlayerEntity player;

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "findRecipeForMatrixClient")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$findRecipeClient(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world, this.player);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "findRecipeForMatrix")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$findRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world, this.player);
  }
}
