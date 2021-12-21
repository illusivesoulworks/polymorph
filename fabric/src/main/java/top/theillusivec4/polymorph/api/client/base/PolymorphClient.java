package top.theillusivec4.polymorph.api.client.base;

import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

public interface PolymorphClient {

  Optional<RecipesWidget> getWidget(HandledScreen<?> pHandledScreen);

  void registerWidget(RecipesWidgetFactory pFactory);

  Optional<Slot> findCraftingResultSlot(HandledScreen<?> pHandledScreen);

  interface RecipesWidgetFactory {

    RecipesWidget createWidget(HandledScreen<?> pHandledScreen);
  }
}
