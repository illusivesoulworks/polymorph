package top.theillusivec4.polymorph.client.recipe;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.api.type.IRecipeController;

public class RecipeControllerHub {

  private static IRecipeController<? extends IInventory, ? extends IRecipe<?>> controller = null;

  public static Optional<IRecipeController<? extends IInventory, ? extends IRecipe<?>>> getController() {
    return Optional.ofNullable(controller);
  }

  public static void startController(ContainerScreen<?> screen) {
    PolymorphClientApi.getInstance().getRecipeController(screen)
        .ifPresent(result -> controller = result);
  }

  public static void clear() {
    controller = null;
  }
}
