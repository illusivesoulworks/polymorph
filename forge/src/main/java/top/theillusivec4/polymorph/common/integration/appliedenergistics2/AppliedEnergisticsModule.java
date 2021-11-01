package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.PatternTermContainer;
import appeng.container.slot.CraftingTermSlot;
import appeng.container.slot.PatternTermSlot;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermContainer;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphNetwork.register(CPacketCallUpdate.class, CPacketCallUpdate::encode,
        CPacketCallUpdate::decode, CPacketCallUpdate::handle);
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen.getContainer() instanceof CraftingTermContainer) {

        for (Slot inventorySlot : pContainerScreen.getContainer().inventorySlots) {

          if (inventorySlot instanceof CraftingTermSlot) {
            return new PlayerRecipesWidget(pContainerScreen, inventorySlot);
          }
        }
      } else if (pContainerScreen.getContainer() instanceof PatternTermContainer) {

        for (Slot inventorySlot : pContainerScreen.getContainer().inventorySlots) {

          if (inventorySlot instanceof PatternTermSlot) {
            return new PatternTermRecipesWidget(pContainerScreen,
                (PatternTermContainer) pContainerScreen.getContainer(), inventorySlot);
          }
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe) {

      if (container instanceof CraftingTermContainer) {
        ((AccessorCraftingTermContainer) container).setCurrentRecipe((ICraftingRecipe) recipe);
        container.onCraftMatrixChanged(((CraftingTermContainer) container).getPlayerInventory());
        return true;
      } else if (container instanceof PatternTermContainer) {
        ((AccessorPatternTermContainer) container).setCurrentRecipe((ICraftingRecipe) recipe);
        ((AccessorPatternTermContainer) container).callGetAndUpdateOutput();
        return true;
      }
    }
    return false;
  }
}
