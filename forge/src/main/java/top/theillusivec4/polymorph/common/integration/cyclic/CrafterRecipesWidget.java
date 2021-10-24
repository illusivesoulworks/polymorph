package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.client.recipe.widget.AbstractProcessorRecipesWidget;

public class CrafterRecipesWidget extends AbstractProcessorRecipesWidget {

  private final ContainerCrafter container;

  public CrafterRecipesWidget(ContainerScreen<?> screen, ContainerCrafter containerCrafter,
                              CraftingInventory inventory) {
    super(screen, 9);
    this.container = containerCrafter;
  }

  @Override
  public Slot getOutputSlot() {
    int index = TileCrafter.GRID_NUM_COLS * TileCrafter.GRID_NUM_ROWS +
        TileCrafter.IO_NUM_COLS * TileCrafter.IO_NUM_ROWS * 2;
    return this.container.getSlot(index);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return null;
  }
}
