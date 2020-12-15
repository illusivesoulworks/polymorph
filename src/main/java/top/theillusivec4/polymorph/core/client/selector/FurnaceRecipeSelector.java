package top.theillusivec4.polymorph.core.client.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.core.Polymorph;

public class FurnaceRecipeSelector
    extends AbstractRecipeSelector<Inventory, AbstractCookingRecipe> {

  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeSelector(HandledScreen<?> screen, FurnaceProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    ItemStack currentStack = this.provider.getInventory().getStack(0);

    if (!ItemStack.areItemsEqual(currentStack, lastStack)) {
      this.lastStack = currentStack;
      this.fetchRecipes();
    }
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {
    Polymorph.getLoader().getPacketVendor().sendSetRecipe(recipe.getId().toString());
  }

  @Override
  public void highlightRecipe(String recipe) {
    this.recipeSelectorGui.highlightButton(recipe);
  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes, World world, boolean refresh,
                         String selected) {
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;

    if (!recipes.isEmpty()) {
      this.highlightRecipe(selected.isEmpty() ? recipes.get(0).getId().toString() : selected);
    }
  }

  @Override
  public void setRecipes(Set<String> recipeIds, World world, boolean refresh, String selected) {
    List<AbstractCookingRecipe> recipes = new ArrayList<>();
    recipeIds.forEach(
        id -> world.getRecipeManager().get(new Identifier(id)).ifPresent(recipe -> {
          if (recipe instanceof AbstractCookingRecipe) {
            recipes.add((AbstractCookingRecipe) recipe);
          }
        }));
    recipes.sort(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()));
    this.setRecipes(recipes, world, false, selected);
  }
}
