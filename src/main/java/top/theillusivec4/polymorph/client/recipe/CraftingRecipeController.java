package top.theillusivec4.polymorph.client.recipe;

import java.util.List;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class CraftingRecipeController
    extends AbstractRecipeController<CraftingInventory, CraftingRecipe> {

  final CraftingInventory craftingInventory;
  final Slot outputSlot;

  public CraftingRecipeController(HandledScreen<?> handledScreen,
                                  CraftingInventory craftingInventory,
                                  Slot outputSlot) {
    super(handledScreen);
    this.craftingInventory = craftingInventory;
    this.outputSlot = outputSlot;
    this.init();
  }

  @Override
  public CraftingInventory getInventory() {
    return this.craftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  public List<? extends CraftingRecipe> getRecipes(World world) {
    return world.getRecipeManager().getAllMatches(RecipeType.CRAFTING, this.getInventory(), world);
  }
}
