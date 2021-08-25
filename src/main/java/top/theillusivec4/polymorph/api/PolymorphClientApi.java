package top.theillusivec4.polymorph.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.type.RecipeController;
import top.theillusivec4.polymorph.client.recipe.CraftingRecipeController;

public class PolymorphClientApi {

  private static final List<Function<HandledScreen<?>, RecipeController<?, ?>>> CONTROLLERS =
      new ArrayList<>();
  private static final PolymorphClientApi INSTANCE = new PolymorphClientApi();

  public static PolymorphClientApi getInstance() {
    return INSTANCE;
  }

  public Optional<RecipeController<?, ?>> getRecipeController(HandledScreen<?> handledScreen) {

    for (Function<HandledScreen<?>, RecipeController<?, ?>> controllerFunction : CONTROLLERS) {
      RecipeController<?, ?> controller = controllerFunction.apply(handledScreen);

      if (controller != null) {
        return Optional.of(controller);
      }
    }
    Slot resultSlot = null;
    CraftingInventory craftingInventory = null;
    ScreenHandler screenHandler = handledScreen.getScreenHandler();

    for (Slot slot : screenHandler.slots) {

      if (resultSlot == null && slot.inventory instanceof CraftingResultInventory) {
        resultSlot = slot;
      } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
        craftingInventory = (CraftingInventory) slot.inventory;
      }

      if (resultSlot != null && craftingInventory != null) {
        break;
      }
    }
    return Optional.ofNullable(resultSlot != null && craftingInventory != null ?
        new CraftingRecipeController(handledScreen, craftingInventory, resultSlot) : null);
  }

  public void addRecipeController(
      Function<HandledScreen<?>, RecipeController<?, ?>> controllerFunction) {
    CONTROLLERS.add(controllerFunction);
  }
}
