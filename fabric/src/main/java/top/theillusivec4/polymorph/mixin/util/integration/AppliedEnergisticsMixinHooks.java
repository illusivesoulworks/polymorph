package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class AppliedEnergisticsMixinHooks {

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getResult(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world,
      PlayerEntity player) {
    List<T> recipes = recipeManager.getAllMatches(type, inventory, world);

    if (recipes.isEmpty()) {

      if (player.getEntityWorld().isClient()) {
        RecipeControllerHub.getController().ifPresent(
            recipeController -> recipeController.setRecipes(new HashSet<>(),
                player.getEntityWorld(), null));
      }
      return Optional.empty();
    }
    T result = null;
    Set<Identifier> ids = new HashSet<>();

    for (T recipe : recipes) {
      Identifier id = recipe.getId();

      if (result == null &&
          CraftingPlayers.getRecipe(player).map(identifier -> identifier.equals(id))
              .orElse(false)) {
        result = recipe;
      }
      ids.add(id);
    }

    if (player.getEntityWorld().isClient()) {
      RecipeControllerHub.getController().ifPresent(
          recipeController -> recipeController.setRecipes(ids, player.getEntityWorld(), null));
    }
    return Optional.of(result != null ? result : recipes.get(0));
  }
}
