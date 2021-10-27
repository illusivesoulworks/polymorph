package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public class PlayerRecipesWidget extends AbstractRecipesWidget {

  final Slot outputSlot;

  public PlayerRecipesWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen);
    this.outputSlot = outputSlot;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendPlayerRecipeSelectionC2S(pResourceLocation);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
