package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public abstract class PersistentRecipesWidget extends AbstractRecipesWidget
    implements ITickingRecipesWidget {

  private NonNullList<ItemStack> lastInput;

  public PersistentRecipesWidget(ContainerScreen<?> pContainerScreen) {
    super(pContainerScreen);
    this.lastInput = NonNullList.create();
  }

  protected abstract NonNullList<ItemStack> getInput();

  @Override
  public void tick() {
    boolean changed = false;
    NonNullList<ItemStack> currentInput = this.getInput();
    this.lastInput = validateList(this.lastInput, currentInput.size());

    for (int i = 0; i < currentInput.size(); i++) {
      ItemStack lastStack = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);

      if (!ItemStack.areItemStacksEqual(lastStack, currentStack)) {
        changed = true;
      }
      this.lastInput.set(i, currentStack.copy());
    }

    if (changed) {
      PolymorphApi.common().getPacketDistributor().sendRecipesRequestC2S();
    }
  }

  private NonNullList<ItemStack> validateList(NonNullList<ItemStack> pList, int pSize) {

    if (pList.size() == pSize) {
      return pList;
    } else {
      NonNullList<ItemStack> resized = NonNullList.withSize(pSize, ItemStack.EMPTY);

      for (int i = 0; i < Math.min(resized.size(), pList.size()); i++) {
        resized.set(i, pList.get(i));
      }
      return resized;
    }
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendRecipeSelectionC2S(pResourceLocation);
  }
}
