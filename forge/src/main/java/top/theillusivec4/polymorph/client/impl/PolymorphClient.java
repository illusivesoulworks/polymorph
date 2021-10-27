package top.theillusivec4.polymorph.client.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
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
  public Optional<Slot> findCraftingResultSlot(ContainerScreen<?> pContainerScreen) {
    Container container = pContainerScreen.getContainer();

    for (Slot slot : container.inventorySlots) {

      if (slot.inventory instanceof CraftResultInventory) {
        return Optional.of(slot);
      }
    }
    return Optional.empty();
  }
}
