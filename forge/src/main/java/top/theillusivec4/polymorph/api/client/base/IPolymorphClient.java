package top.theillusivec4.polymorph.api.client.base;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public interface IPolymorphClient {

  Optional<IRecipesWidget> getWidget(ContainerScreen<?> containerScreen);

  void registerWidget(IRecipesWidgetFactory factory);

  Optional<Pair<Slot, CraftingInventory>> getCraftingPair(ContainerScreen<?> containerScreen);

  interface IRecipesWidgetFactory {

    IRecipesWidget createWidget(ContainerScreen<?> screen);
  }
}
