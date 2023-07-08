package top.theillusivec4.polymorph.mixin.integration.occultism;

import com.github.klikli_dev.occultism.common.container.storage.StorageControllerContainerBase;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(StorageControllerContainerBase.class)
public class MixinStorageControllerContainerBase {

  @Shadow
  public Player player;

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "findRecipeForMatrixClient")
  private <C extends Container, T extends Recipe<C>> Optional<T> polymorph$findRecipeClient(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world) {
    return RecipeSelection.getPlayerRecipe(this.player.containerMenu, type, inventory, world,
        this.player);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "findRecipeForMatrix")
  private <C extends Container, T extends Recipe<C>> Optional<T> polymorph$findRecipe(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world) {
    return RecipeSelection.getPlayerRecipe(this.player.containerMenu, type, inventory, world,
        this.player);
  }
}
