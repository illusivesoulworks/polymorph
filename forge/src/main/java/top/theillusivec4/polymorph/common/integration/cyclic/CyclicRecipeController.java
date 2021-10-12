package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.client.recipe.controller.AbstractRecipeController;
import top.theillusivec4.polymorph.common.util.FieldAccessor;
import top.theillusivec4.polymorph.mixin.util.integration.CyclicHooks;

public class CyclicRecipeController
    extends AbstractRecipeController<CraftingInventory, ICraftingRecipe> {

  private final ContainerCrafter container;
  private final TileCrafter crafter;

  public CyclicRecipeController(ContainerScreen<?> screen, ContainerCrafter containerCrafter) {
    super(screen);
    this.container = containerCrafter;
    this.crafter = (TileCrafter) FieldAccessor.read(containerCrafter, "tile");
    this.init();
  }

  @Override
  public CraftingInventory getInventory() {
    CraftingInventory craftingInventory =
        new CraftingInventory(new CyclicHooks.FakeContainer(ContainerType.CRAFTING, 219895893), 3,
            3);
    LazyOptional<IItemHandler> opt =
        ((LazyOptional<IItemHandler>) FieldAccessor.read(crafter, "gridCap"));

    if (opt != null) {
      IItemHandler gridHandler = opt.orElse(null);

      if (gridHandler != null) {

        for (int i = 0; i < 9; ++i) {
          craftingInventory.setInventorySlotContents(i, gridHandler.getStackInSlot(i));
        }
      }
    }
    return craftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    int index = TileCrafter.GRID_NUM_COLS * TileCrafter.GRID_NUM_ROWS +
        TileCrafter.IO_NUM_COLS * TileCrafter.IO_NUM_ROWS * 2;
    return this.container.getSlot(index);
  }
}
