package top.theillusivec4.polymorph;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;

public class ClientCraftingManager {

  private static final List<IRecipe<CraftingInventory>> resultsList = new ArrayList<>();

  public static void setResultsList(List<ICraftingRecipe> list) {
    resultsList.clear();
    resultsList.addAll(list);
    ImageButton button = ScreenEventHandler.switchButton;

    if (button != null) {
      button.visible = resultsList.size() > 1;
    }
    RecipeSelectionGui recipeSelectionGui = ScreenEventHandler.recipeSelectionGui;

    if (recipeSelectionGui != null) {
      recipeSelectionGui.setRecipes(resultsList);
    }
  }

  public static List<IRecipe<CraftingInventory>> getResultsList() {
    return resultsList;
  }
}
