package top.theillusivec4.polymorph.api.type;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;

public interface IFurnaceProvider extends IPolyProvider<IInventory> {

  default IRecipeSelector<IInventory, AbstractCookingRecipe> createSelector(
      ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().createFurnaceSelector(screen, this);
  }
}
