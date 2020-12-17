package top.theillusivec4.polymorph.client.selector;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;

public class RecipeSelectorManager {

  private static IRecipeSelector<? extends IInventory, ? extends IRecipe<?>> selector = null;
  private static ResourceLocation preferredRecipe = null;

  public static Optional<IRecipeSelector<? extends IInventory, ? extends IRecipe<?>>> getSelector() {
    return Optional.ofNullable(selector);
  }

  public static boolean tryCreate(ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().getProvider(screen.getContainer()).map(provider -> {
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

  public static void setPreferredRecipe(ResourceLocation id) {
    preferredRecipe = id;
  }

  public static Optional<ResourceLocation> getPreferredRecipe() {
    return Optional.ofNullable(preferredRecipe);
  }
}
