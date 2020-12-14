package top.theillusivec4.polymorph.client.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;

public class FurnaceRecipeSelector extends RecipeSelector<IInventory, AbstractCookingRecipe> {

  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeSelector(ContainerScreen<?> screen, IFurnaceProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    ItemStack currentStack = this.provider.getInventory().getStackInSlot(0);

    if (!ItemStack.areItemsEqual(currentStack, lastStack)) {
      this.lastStack = currentStack;
      this.fetchRecipes();
    }
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {
    NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSetRecipe(recipe.getId().toString()));
  }

  @Override
  public void highlightRecipe(String recipe) {
    this.recipeSelectorGui.highlightButton(recipe);
  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes, World world, boolean refresh, String selected) {
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
        id -> world.getRecipeManager().getRecipe(new ResourceLocation(id)).ifPresent(recipe -> {
          if (recipe instanceof AbstractCookingRecipe) {
            recipes.add((AbstractCookingRecipe) recipe);
          }
        }));
    recipes.sort(Comparator.comparing((recipe) -> recipe.getRecipeOutput().getTranslationKey()));
    this.setRecipes(recipes, world, false, selected);
  }
}
