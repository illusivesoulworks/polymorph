package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class CrafterRecipesWidget extends PersistentRecipesWidget {

  private final ContainerCrafter container;

  public CrafterRecipesWidget(ContainerScreen<?> screen, ContainerCrafter containerCrafter) {
    super(screen);
    this.container = containerCrafter;
  }

  @Override
  public Slot getOutputSlot() {
    int index = TileCrafter.GRID_NUM_COLS * TileCrafter.GRID_NUM_ROWS +
        TileCrafter.IO_NUM_COLS * TileCrafter.IO_NUM_ROWS * 2;
    return this.container.getSlot(index);
  }

  @Override
  public int getYPos() {
    return super.getYPos() + 3;
  }
}
