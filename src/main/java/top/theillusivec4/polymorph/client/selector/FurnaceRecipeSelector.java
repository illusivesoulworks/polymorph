package top.theillusivec4.polymorph.client.selector;

import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;

public class FurnaceRecipeSelector extends RecipeSelector<IInventory, AbstractCookingRecipe> {

  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeSelector(ContainerScreen<?> screen, IFurnaceProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    if (!ItemStack.areItemsEqual(this.provider.getInventory().getStackInSlot(0), lastStack)) {
      this.fetchRecipes();
    }
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {

  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes, World world, boolean refresh) {
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;
  }

  @Override
  public void setRecipes(Set<String> recipes, World world, boolean refresh) {

  }
}
