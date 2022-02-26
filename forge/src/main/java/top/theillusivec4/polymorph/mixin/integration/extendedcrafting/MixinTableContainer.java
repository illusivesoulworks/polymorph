package top.theillusivec4.polymorph.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.container.BasicAutoTableContainer;
import com.blakebr0.extendedcrafting.container.BasicTableContainer;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(BasicTableContainer.class)
public abstract class MixinTableContainer extends AbstractContainerMenu {

  protected MixinTableContainer(@Nullable MenuType<?> pMenuType, int pContainerId) {
    super(pMenuType, pContainerId);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "slotsChanged")
  private <C extends Container, T extends Recipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world,
      Container unused) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world, this.slots);
  }
}
