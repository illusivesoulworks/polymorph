package top.theillusivec4.polymorph.common.integration.fabricfurnaces;

import draylar.fabricfurnaces.entity.BaseFurnaceEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import top.theillusivec4.polymorph.common.component.AbstractHighlightedRecipeData;

public class FabricFurnaceRecipeData extends AbstractHighlightedRecipeData<BaseFurnaceEntity> {

  public FabricFurnaceRecipeData(BaseFurnaceEntity pOwner) {
    super(pOwner);
  }

  @Override
  protected DefaultedList<ItemStack> getInput() {
    return DefaultedList.copyOf(ItemStack.EMPTY, this.getOwner().getStack(2));
  }

  @Override
  public boolean isEmpty() {
    return this.getInput().get(0).isEmpty();
  }
}
