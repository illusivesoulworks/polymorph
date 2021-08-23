package top.theillusivec4.polymorph.mixin.integration;

import appeng.container.AEBaseContainer;
import appeng.container.me.items.CraftingTermContainer;
import java.util.Optional;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(CraftingTermContainer.class)
public abstract class MixinAppliedEnergistics extends AEBaseContainer {

  public MixinAppliedEnergistics(ScreenHandlerType<?> containerType, int id,
                                 PlayerInventory playerInventory, Object host) {
    super(containerType, id, playerInventory, host);
  }

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/recipe/RecipeManager.getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "onContentChanged")
  private <C extends Inventory, T extends Recipe<C>> Optional<T> polymorph$getFirstMatch(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world, Inventory unused) {
    return MixinHooks.getResult(recipeManager, type, inventory, world,
        this.getPlayerInventory().player);
  }
}
