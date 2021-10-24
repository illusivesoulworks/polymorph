package top.theillusivec4.polymorph.client.impl;

import com.mojang.datafixers.util.Pair;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;

public class PolymorphClient implements IPolymorphClient {

  private static final IPolymorphClient INSTANCE = new PolymorphClient();

  public static IPolymorphClient get() {
    return INSTANCE;
  }

  private final List<IRecipesWidgetFactory> widgetFactories = new LinkedList<>();

  public Optional<IRecipesWidget> getWidget(ContainerScreen<?> pContainerScreen) {

    for (IRecipesWidgetFactory factory : this.widgetFactories) {
      IRecipesWidget widget = factory.createWidget(pContainerScreen);

      if (widget != null) {
        return Optional.of(widget);
      }
    }
    return Optional.empty();
  }


  @Override
  public void registerWidget(IRecipesWidgetFactory pFactory) {
    this.widgetFactories.add(pFactory);
  }

  @Override
  public Optional<Pair<Slot, CraftingInventory>> getCraftingPair(
      ContainerScreen<?> containerScreen) {
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
    return resultSlot != null && craftingInventory != null ?
        Optional.of(new Pair<>(resultSlot, craftingInventory)) : Optional.empty();
  }
}
