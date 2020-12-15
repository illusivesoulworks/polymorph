package top.theillusivec4.polymorph.core.client.selector;

import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.RecipeSelector;

public class RecipeSelectorManager {

  private static RecipeSelector<? extends Inventory, ? extends Recipe<?>> selector = null;
  private static ItemStack preferredStack = ItemStack.EMPTY;

  public static Optional<RecipeSelector<? extends Inventory, ? extends Recipe<?>>> getSelector() {
    return Optional.ofNullable(selector);
  }

  public static boolean tryCreate(HandledScreen<?> screen) {
    return PolymorphApi.getInstance().getProvider(screen.getScreenHandler()).map(provider -> {
      if (provider.isActive()) {
        selector = provider.createSelector(screen);
        return true;
      }
      return false;
    }).orElse(false);
  }

  public static void clear() {
    selector = null;
  }

  public static void setPreferredStack(ItemStack stack) {
    preferredStack = stack;
  }

  public static ItemStack getPreferredStack() {
    return preferredStack;
  }
}
