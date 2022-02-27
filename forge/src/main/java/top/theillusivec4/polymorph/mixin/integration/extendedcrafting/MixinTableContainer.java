package top.theillusivec4.polymorph.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.container.BasicTableContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(BasicTableContainer.class)
public abstract class MixinTableContainer extends Container {

  protected MixinTableContainer(@Nullable ContainerType<?> type, int id) {
    super(type, id);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "onCraftMatrixChanged")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      IInventory unused) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world, this.inventorySlots);
  }
}
