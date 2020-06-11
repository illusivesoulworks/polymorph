package top.theillusivec4.polymorph.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketSetOutput;

public class ClientCraftingManager {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");

  private static ClientCraftingManager instance;

  private RecipeSelectionGui recipeSelectionGui;

  private CraftingInventory currentCraftingMatrix;
  private IRecipe<CraftingInventory> lastPlacedRecipe;
  private IRecipe<CraftingInventory> lastSelectedRecipe;

  private ImageButton switchButton;

  private ClientCraftingManager(ContainerScreen<?> screen) {
    this.recipeSelectionGui = new RecipeSelectionGui(this);
    int x = screen.width / 2;
    int y = screen.height / 2;
    this.switchButton = new ImageButton(x + 36, y - 72, 16, 16, 0, 0, 17, SWITCH,
        clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
  }

  public static Optional<ClientCraftingManager> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static ClientCraftingManager refreshInstance(ContainerScreen<?> screen) {
    instance = new ClientCraftingManager(screen);
    return instance;
  }

  public void update() {
    ClientWorld world = Minecraft.getInstance().world;

    if (world != null) {
      this.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {

        if (this.getLastPlacedRecipe().map(recipe -> !recipe.matches(craftingInventory, world))
            .orElse(true)) {
          List<ICraftingRecipe> recipes = world.getRecipeManager()
              .getRecipes(IRecipeType.CRAFTING, craftingInventory, world);
          Polymorph.LOGGER.info("setting recipes");
          recipeSelectionGui.setRecipes(recipes);
          this.setLastPlacedRecipe(recipes.isEmpty() ? null : recipes.get(0));
        }
        this.getLastSelectedRecipe().ifPresent(recipe -> {
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
            this.setLastSelectedRecipe(null);
          }
        });
      });
    }
  }

  public Optional<IRecipe<CraftingInventory>> getLastPlacedRecipe() {
    return Optional.ofNullable(lastPlacedRecipe);
  }

  public void setLastPlacedRecipe(IRecipe<CraftingInventory> recipe) {
    lastPlacedRecipe = recipe;
  }

  public Optional<IRecipe<CraftingInventory>> getLastSelectedRecipe() {
    return Optional.ofNullable(lastSelectedRecipe);
  }

  public void setLastSelectedRecipe(IRecipe<CraftingInventory> recipe) {
    lastSelectedRecipe = recipe;
  }

  public Optional<CraftingInventory> getCurrentCraftingMatrix() {
    return Optional.ofNullable(currentCraftingMatrix);
  }

  public void setCurrentCraftingMatrix(CraftingInventory craftingMatrix) {
    currentCraftingMatrix = craftingMatrix;
  }

  public RecipeSelectionGui getRecipeSelectionGui() {
    return this.recipeSelectionGui;
  }

  public ImageButton getSwitchButton() {
    return this.switchButton;
  }

  public void setSwitchButton(ImageButton button) {
    this.switchButton = button;
  }
}
