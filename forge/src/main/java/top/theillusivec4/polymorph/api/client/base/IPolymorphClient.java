package top.theillusivec4.polymorph.api.client.base;

import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;

public interface IPolymorphClient {

  Optional<IRecipesWidget> getWidget(ContainerScreen<?> pContainerScreen);

  void registerWidget(IRecipesWidgetFactory pFactory);

  Optional<Slot> findCraftingResultSlot(ContainerScreen<?> pContainerScreen);

  interface IRecipesWidgetFactory {

    IRecipesWidget createWidget(ContainerScreen<?> pContainerScreen);
  }
}
