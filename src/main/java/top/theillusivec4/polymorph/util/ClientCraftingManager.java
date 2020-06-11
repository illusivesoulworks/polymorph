package top.theillusivec4.polymorph.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketSetOutput;

public class ClientCraftingManager {

  private static final List<IRecipe<CraftingInventory>> recipesList = new ArrayList<>();

  private static RecipeSelectionGui recipeSelectionGui;
  private static CraftingInventory currentCraftingMatrix;
  private static IRecipe<CraftingInventory> lastPlacedRecipe;
  private static IRecipe<CraftingInventory> lastSelectedRecipe;

  private static ImageButton switchButton;

  public static void updateOutput() {
    ClientWorld world = Minecraft.getInstance().world;

    if (world != null) {
      getCurrentCraftingMatrix().ifPresent(craftingInventory -> {

        if (getLastPlacedRecipe().map(recipe -> !recipe.matches(craftingInventory, world))
            .orElse(true)) {
          List<ICraftingRecipe> recipes = world.getRecipeManager()
              .getRecipes(IRecipeType.CRAFTING, craftingInventory, world);
          Polymorph.LOGGER.info("setting recipes");
          setRecipesList(recipes);
          setLastPlacedRecipe(recipes.isEmpty() ? null : recipes.get(0));
        }
        getLastSelectedRecipe().ifPresent(recipe -> {
          if (recipe.matches(craftingInventory, world)) {
            ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

            if (playerEntity != null) {
              Container container = playerEntity.openContainer;

              if (container instanceof WorkbenchContainer) {
                WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;
                ItemStack stack = recipe.getCraftingResult(craftingInventory);
                workbenchContainer.getSlot(workbenchContainer.getOutputSlot())
                    .putStack(stack.copy());
                NetworkHandler.INSTANCE
                    .send(PacketDistributor.SERVER.noArg(), new CPacketSetOutput(stack));
              }
            }
          } else {
            setLastSelectedRecipe(null);
          }
        });
      });
    }
  }

  public static Optional<IRecipe<CraftingInventory>> getLastPlacedRecipe() {
    return Optional.ofNullable(lastPlacedRecipe);
  }

  public static void setLastPlacedRecipe(IRecipe<CraftingInventory> recipe) {
    lastPlacedRecipe = recipe;
  }

  public static Optional<IRecipe<CraftingInventory>> getLastSelectedRecipe() {
    return Optional.ofNullable(lastSelectedRecipe);
  }

  public static void setLastSelectedRecipe(IRecipe<CraftingInventory> recipe) {
    lastSelectedRecipe = recipe;
  }

  public static Optional<CraftingInventory> getCurrentCraftingMatrix() {
    return Optional.ofNullable(currentCraftingMatrix);
  }

  public static void setCurrentCraftingMatrix(CraftingInventory craftingMatrix) {
    currentCraftingMatrix = craftingMatrix;
  }

  public static Optional<RecipeSelectionGui> getRecipeSelectionGui() {
    return Optional.ofNullable(recipeSelectionGui);
  }

  public static RecipeSelectionGui createRecipeSelectionGui() {
    recipeSelectionGui = new RecipeSelectionGui();
    return recipeSelectionGui;
  }

  public static void setRecipesList(List<ICraftingRecipe> list) {
    recipesList.clear();
    recipesList.addAll(list);
    getSwitchButton().ifPresent(switchButton -> switchButton.visible = recipesList.size() > 1);
    getRecipeSelectionGui().ifPresent(gui -> recipeSelectionGui.setRecipes(recipesList));
  }

  public static List<IRecipe<CraftingInventory>> getRecipesList() {
    return recipesList;
  }

  public static Optional<ImageButton> getSwitchButton() {
    return Optional.ofNullable(switchButton);
  }

  public static void setSwitchButton(ImageButton button) {
    switchButton = button;
  }
}
