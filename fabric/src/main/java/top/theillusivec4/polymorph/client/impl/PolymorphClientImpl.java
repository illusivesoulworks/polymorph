package top.theillusivec4.polymorph.client.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.client.base.PolymorphClient;
import top.theillusivec4.polymorph.api.client.base.RecipesWidget;

public class PolymorphClientImpl implements PolymorphClient {

  private static final PolymorphClient INSTANCE = new PolymorphClientImpl();

  public static PolymorphClient get() {
    return INSTANCE;
  }

  private final List<RecipesWidgetFactory> widgetFactories = new LinkedList<>();

  @Override
  public Optional<RecipesWidget> getWidget(HandledScreen<?> pHandledScreen) {

    for (RecipesWidgetFactory factory : this.widgetFactories) {
      RecipesWidget widget = factory.createWidget(pHandledScreen);

      if (widget != null) {
        return Optional.of(widget);
      }
    }
    return Optional.empty();
  }

  @Override
  public void registerWidget(RecipesWidgetFactory pFactory) {
    this.widgetFactories.add(pFactory);
  }

  @Override
  public Optional<Slot> findCraftingResultSlot(HandledScreen<?> pHandledScreen) {
    ScreenHandler screenHandler = pHandledScreen.getScreenHandler();

    for (Slot slot : screenHandler.slots) {

      if (slot.inventory instanceof CraftingResultInventory) {
        return Optional.of(slot);
      }
    }
    return Optional.empty();
  }
}
