package top.theillusivec4.polymorph.client;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class RecipeSelectorManager {

  private static RecipeSelector selector = null;
  private static ItemStack preferredStack = ItemStack.EMPTY;
  private static IRecipe<CraftingInventory> lastPlacedRecipe;
  private static List<ICraftingRecipe> lastRecipesList;
  private static IRecipe<CraftingInventory> lastSelectedRecipe;

  public static Optional<RecipeSelector> getSelector() {
    return Optional.ofNullable(selector);
  }

  public static boolean tryCreate(ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().getProvider(screen.getContainer()).map(provider -> {
      if (provider.isValid()) {
        selector = new RecipeSelector(screen, provider);
        return true;
      } else {
        return false;
      }
    }).orElse(false);
  }

  public static void clear() {
    selector = null;
  }

  public static void update() {

    if (selector != null && selector.updatable()) {
      selector.markUpdate();
    }
  }

  public static void setPreferredStack(ItemStack stack) {
    preferredStack = stack;
  }

  public static ItemStack getPreferredStack() {
    return preferredStack;
  }

  public static Optional<List<ICraftingRecipe>> getLastRecipesList() {
    return Optional.ofNullable(lastRecipesList);
  }

  public static void setLastRecipesList(List<ICraftingRecipe> recipesList) {
    lastRecipesList = recipesList;
  }

  public static Optional<IRecipe<CraftingInventory>> getLastPlacedRecipe() {
    return Optional.ofNullable(lastPlacedRecipe);
  }

  public static void setLastPlacedRecipe(IRecipe<CraftingInventory> recipe) {
    lastPlacedRecipe = recipe;
  }

  public static Optional<IRecipe<CraftingInventory>> getLastSelectedRecipe() {
    return Optional.ofNullable(lastSelectedRecipe);
  }

  public static void setLastSelectedRecipe(IRecipe<CraftingInventory> recipe) {
    lastSelectedRecipe = recipe;
  }
}
