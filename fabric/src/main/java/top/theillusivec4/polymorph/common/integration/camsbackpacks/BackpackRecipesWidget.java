package top.theillusivec4.polymorph.common.integration.camsbackpacks;

import dev.cammiescorner.camsbackpacks.client.CamsBackpacksClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class BackpackRecipesWidget extends PlayerRecipesWidget {

  private Slot backpackOutput;
  private Slot inventoryOutput;

  public BackpackRecipesWidget(HandledScreen<?> pHandledScreen) {
    super(pHandledScreen, null);
  }

  @Override
  public Slot getOutputSlot() {

    if (CamsBackpacksClient.backpackScreenIsOpen) {

      if (backpackOutput == null) {
        backpackOutput = findOutputSlot();
      }
      return backpackOutput;
    } else {

      if (inventoryOutput == null) {
        inventoryOutput = findOutputSlot();
      }
      return inventoryOutput;
    }
  }

  @Override
  public int getXPos() {

    if (CamsBackpacksClient.backpackScreenIsOpen) {
      return this.getOutputSlot().x + 22;
    }
    return super.getXPos();
  }

  @Override
  public int getYPos() {

    if (CamsBackpacksClient.backpackScreenIsOpen) {
      return this.getOutputSlot().y;
    }
    return super.getYPos();
  }

  private Slot findOutputSlot() {
    return PolymorphApi.client().findCraftingResultSlot(this.handledScreen)
        .orElse(this.handledScreen.getScreenHandler().getSlot(0));
  }
}
