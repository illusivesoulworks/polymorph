package top.theillusivec4.polymorph.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.type.IRecipeController;
import top.theillusivec4.polymorph.client.recipe.controller.CraftingRecipeController;

public final class PolymorphClientApi {

  private static final List<Function<ContainerScreen<?>, IRecipeController<?, ?>>> CONTROLLERS =
      new ArrayList<>();
  private static final PolymorphClientApi INSTANCE = new PolymorphClientApi();

  public static PolymorphClientApi getInstance() {
    return INSTANCE;
  }

  public Optional<IRecipeController<?, ?>> getRecipeController(ContainerScreen<?> containerScreen) {

    for (Function<ContainerScreen<?>, IRecipeController<?, ?>> controllerFunction : CONTROLLERS) {
      IRecipeController<?, ?> controller = controllerFunction.apply(containerScreen);

      if (controller != null) {
        return Optional.of(controller);
      }
    }
    Slot resultSlot = null;
    CraftingInventory craftingInventory = null;
    Container container = containerScreen.getContainer();

    for (Slot slot : container.inventorySlots) {

      if (resultSlot == null && slot.inventory instanceof CraftResultInventory) {
        resultSlot = slot;
      } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
        craftingInventory = (CraftingInventory) slot.inventory;
      }

      if (resultSlot != null && craftingInventory != null) {
        break;
      }
    }
    return Optional.ofNullable(resultSlot != null && craftingInventory != null ?
        new CraftingRecipeController(containerScreen, craftingInventory, resultSlot) : null);
  }

  public void addRecipeController(
      Function<ContainerScreen<?>, IRecipeController<?, ?>> controllerFunction) {
    CONTROLLERS.add(controllerFunction);
  }
}
