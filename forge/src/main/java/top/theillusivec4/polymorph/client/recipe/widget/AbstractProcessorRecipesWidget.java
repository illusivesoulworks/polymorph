package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public abstract class AbstractProcessorRecipesWidget extends AbstractRecipesWidget
    implements ITickingRecipesWidget {

  private final NonNullList<ItemStack> lastInput;

  public AbstractProcessorRecipesWidget(ContainerScreen<?> containerScreen, int inputSize) {
    super(containerScreen);
    this.lastInput = NonNullList.withSize(inputSize, ItemStack.EMPTY);
  }

  @Override
  public void tick() {
    boolean changed = false;
    NonNullList<ItemStack> currentInput = this.getInput();

    for (int i = 0; i < currentInput.size(); i++) {
      ItemStack lastStack = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);

      if (!ItemStack.areItemsEqual(lastStack, currentStack)) {
        changed = true;
      }
      this.lastInput.set(i, currentStack);
    }

    if (changed) {
      PolymorphApi.common().getPacketDistributor().sendRecipesRequestC2S();
    }
  }

  protected abstract NonNullList<ItemStack> getInput();

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendRecipeSelectionC2S(pResourceLocation);
  }
}
