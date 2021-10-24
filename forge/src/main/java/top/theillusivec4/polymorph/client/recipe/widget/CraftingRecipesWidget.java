package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public class CraftingRecipesWidget extends AbstractRecipesWidget {

  final CraftingInventory craftingInventory;
  final Slot outputSlot;

  public CraftingRecipesWidget(ContainerScreen<?> containerScreen,
                               CraftingInventory craftingInventory,
                               Slot outputSlot) {
    super(containerScreen);
    this.craftingInventory = craftingInventory;
    this.outputSlot = outputSlot;
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