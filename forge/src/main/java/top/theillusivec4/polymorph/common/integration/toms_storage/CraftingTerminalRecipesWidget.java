package top.theillusivec4.polymorph.common.integration.toms_storage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.AbstractProcessorRecipesWidget;

public class CraftingTerminalRecipesWidget extends AbstractProcessorRecipesWidget {

  private final Slot outputSlot;
  private final CraftingInventory craftingInventory;

  public CraftingTerminalRecipesWidget(ContainerScreen<?> containerScreen,
                                       CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, 9);
    this.outputSlot = outputSlot;
    this.craftingInventory = craftingInventory;
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    NonNullList<ItemStack> stacks =
        NonNullList.withSize(this.craftingInventory.getSizeInventory(), ItemStack.EMPTY);

    for (int i = 0; i < this.craftingInventory.getSizeInventory(); i++) {
      stacks.set(i, this.craftingInventory.getStackInSlot(i));
    }
    return stacks;
  }

  @Override
  public void highlightRecipe(ResourceLocation recipe) {
    // NO-OP
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendRecipeSelectionC2S(pResourceLocation);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
