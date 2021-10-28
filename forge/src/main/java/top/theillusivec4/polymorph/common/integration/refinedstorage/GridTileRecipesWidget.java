package top.theillusivec4.polymorph.common.integration.refinedstorage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class GridTileRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public GridTileRecipesWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen);
    this.outputSlot = outputSlot;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
