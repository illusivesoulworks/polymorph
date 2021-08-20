package top.theillusivec4.polymorph.common.impl;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.Polymorphable;
import top.theillusivec4.polymorph.api.type.RecipeController;

public class PolymorphApiImpl extends PolymorphApi {

  public static final PolymorphApi INSTANCE = new PolymorphApiImpl();

  @Override
  public Optional<Polymorphable<?, ?>> getPolymorphable(ScreenHandler screenHandler) {
    Slot resultSlot = null;
    CraftingInventory craftingInventory = null;

    for (Slot slot : screenHandler.slots) {

      if (resultSlot == null && slot.inventory instanceof CraftingResultInventory) {
        resultSlot = slot;
      } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
        craftingInventory = (CraftingInventory) slot.inventory;
      }

      if (resultSlot != null && craftingInventory != null) {
        break;
      }
    }
    return Optional.ofNullable(resultSlot != null && craftingInventory != null ?
        new SimpleCraftingPolymorphable(screenHandler, craftingInventory, resultSlot) : null);
  }

  private static class SimpleCraftingPolymorphable
      implements Polymorphable<CraftingInventory, CraftingRecipe> {

    final ScreenHandler screenHandler;
    final CraftingInventory craftingInventory;
    final Slot resultSlot;

    public SimpleCraftingPolymorphable(ScreenHandler screenHandler, CraftingInventory craftingInventory,
                                       Slot resultSlot) {
      this.screenHandler = screenHandler;
      this.craftingInventory = craftingInventory;
      this.resultSlot = resultSlot;
    }

    @Override
    public ScreenHandler getScreenHandler() {
      return this.screenHandler;
    }

    @Override
    public CraftingInventory getInventory() {
      return this.craftingInventory;
    }

    @Override
    public Slot getOutputSlot() {
      return this.resultSlot;
    }

    @Override
    public List<CraftingRecipe> getRecipes(World world, RecipeManager recipeManager) {
      return recipeManager.getAllMatches(RecipeType.CRAFTING, this.craftingInventory, world);
    }

    @Override
    public RecipeController<CraftingInventory, CraftingRecipe> getRecipeController(
        HandledScreen<?> screen) {
      return null;
    }
  }
}
