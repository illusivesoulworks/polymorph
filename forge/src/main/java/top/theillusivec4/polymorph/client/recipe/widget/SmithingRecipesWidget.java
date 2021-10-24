package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public class SmithingRecipesWidget extends AbstractRecipesWidget {

  final IInventory inventory;
  final Slot outputSlot;

  public SmithingRecipesWidget(ContainerScreen<?> screen) {
    super(screen);
    this.outputSlot = screen.getContainer().inventorySlots.get(2);
    this.inventory = screen.getContainer().inventorySlots.get(0).inventory;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendCraftingSelectionC2S(pResourceLocation);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
