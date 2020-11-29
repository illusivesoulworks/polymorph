package top.theillusivec4.polymorph.client.selector;

import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;

public class FurnaceRecipeSelector
    extends RecipeSelector<IInventory, IFurnaceProvider, AbstractCookingRecipe> {

  public FurnaceRecipeSelector(ContainerScreen<?> screen, IFurnaceProvider provider) {
    super(screen, provider);
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {

  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes, World world, boolean refresh) {

  }
}
