package top.theillusivec4.polymorph;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;

public class ClientCraftingManager {

  private static final List<ItemStack> resultsList = new ArrayList<>();

  public static void setResultsList(List<ItemStack> list) {
    resultsList.clear();
    resultsList.addAll(list);
    resultsList.forEach(stack -> Polymorph.LOGGER.info(stack.toString()));
    ImageButton button = ScreenEventHandler.switchButton;

    if (button != null) {
      button.visible = resultsList.size() > 1;
    }
    RecipeSelectionGui recipeSelectionGui = ScreenEventHandler.recipeSelectionGui;

    if (recipeSelectionGui != null) {
      recipeSelectionGui.setStacks(resultsList);
    }
  }

  public static List<ItemStack> getResultsList() {
    return resultsList;
  }
}
