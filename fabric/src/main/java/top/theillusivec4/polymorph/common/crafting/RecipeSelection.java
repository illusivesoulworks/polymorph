package top.theillusivec4.polymorph.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.api.common.component.RecipeData;
import top.theillusivec4.polymorph.api.common.component.StackRecipeData;

public class RecipeSelection {

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getPlayerRecipe(
      ScreenHandler pScreenHandler, RecipeType<T> pType, C pInventory, World pWorld,
      PlayerEntity pPlayer) {
    return getPlayerRecipe(pScreenHandler, pType, pInventory, pWorld, pPlayer, new ArrayList<>());
  }

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getPlayerRecipe(
      ScreenHandler pScreenHandler, RecipeType<T> pType, C pInventory, World pWorld,
      PlayerEntity pPlayer, List<T> pRecipes) {
    Optional<PlayerRecipeData> maybeData = PolymorphApi.common().getRecipeData(pPlayer);
    maybeData.ifPresent(playerRecipeData -> playerRecipeData.setScreenHandler(pScreenHandler));
    return getRecipe(pType, pInventory, pWorld, maybeData, pRecipes);
  }

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getStackRecipe(
      RecipeType<T> pType, C pInventory, World pWorld, ItemStack pStack) {
    Optional<StackRecipeData> maybeData = PolymorphApi.common().getRecipeData(pStack);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getBlockEntityRecipe(
      RecipeType<T> pType, C pInventory, World pWorld, BlockEntity pBlockEntity) {
    Optional<BlockEntityRecipeData> maybeData = PolymorphApi.common().getRecipeData(pBlockEntity);
    return getRecipe(pType, pInventory, pWorld, maybeData, new ArrayList<>());
  }

  private static <T extends Recipe<C>, C extends Inventory> Optional<T> getRecipe(
      RecipeType<T> pType, C pInventory, World pWorld, Optional<? extends RecipeData<?>> pOpt,
      List<T> pRecipes) {

    if (pOpt.isPresent()) {
      return pOpt.flatMap(recipeData -> recipeData.getRecipe(pType, pInventory, pWorld, pRecipes));
    } else {
      return pWorld.getRecipeManager().getAllMatches(pType, pInventory, pWorld).stream()
          .findFirst();
    }
  }
}
