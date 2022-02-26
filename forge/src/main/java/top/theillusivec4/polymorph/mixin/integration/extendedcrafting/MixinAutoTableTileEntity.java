package top.theillusivec4.polymorph.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(AutoTableTileEntity.class)
public class MixinAutoTableTileEntity {

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "tick")
  private static <C extends Container, T extends Recipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world,
      Level unused, BlockPos pos, BlockState state, AutoTableTileEntity tile) {
    return RecipeSelection.getTileEntityRecipe(type, inventory, world, tile);
  }

  @SuppressWarnings("ConstantConditions")
  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "saveRecipe")
  private <C extends Container, T extends Recipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world, int index) {
    return RecipeSelection.getTileEntityRecipe(type, inventory, world,
        (AutoTableTileEntity) (Object) this);
  }
}
