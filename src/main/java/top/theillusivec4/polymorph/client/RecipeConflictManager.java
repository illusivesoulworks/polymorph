package top.theillusivec4.polymorph.client;

import java.lang.reflect.Field;
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
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;

public class RecipeConflictManager {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");
  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(WorkbenchContainer.class, "field_75162_e");

  private static RecipeConflictManager instance;

  private RecipeSelectionGui recipeSelectionGui;

  private CraftingInventory currentCraftingMatrix;
  private IRecipe<CraftingInventory> lastPlacedRecipe;
  private IRecipe<CraftingInventory> lastSelectedRecipe;

  private ImageButton switchButton;
  private boolean needsUpdate;

  private RecipeConflictManager(ContainerScreen<?> screen) {
    int x = screen.width / 2;
    int y = screen.height / 2;
    this.recipeSelectionGui = new RecipeSelectionGui(this, x + 19, y - 100);
    this.switchButton = new ImageButton(x + 36, y - 72, 16, 16, 0, 0, 17, SWITCH,
        clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
    this.switchButton.visible = this.recipeSelectionGui.getButtons().size() > 1;

    if (screen.getContainer() instanceof WorkbenchContainer) {
      WorkbenchContainer workbenchContainer = (WorkbenchContainer) screen.getContainer();
      CraftingInventory craftingMatrix = null;

      try {
        craftingMatrix = (CraftingInventory) CRAFT_MATRIX.get(workbenchContainer);
      } catch (IllegalAccessException e) {
        Polymorph.LOGGER.error("beep beep");
      }
      this.currentCraftingMatrix = craftingMatrix;
    }
  }

  public static Optional<RecipeConflictManager> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static RecipeConflictManager refreshInstance(ContainerScreen<?> screen) {
    instance = new RecipeConflictManager(screen);
    return instance;
  }

  public void update() {
    ClientWorld world = Minecraft.getInstance().world;
    this.needsUpdate = false;

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
                NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                    new CPacketSetRecipe(recipe.getId().toString()));
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

  public void flagUpdate() {
    this.needsUpdate = true;
  }

  public boolean needsUpdate() {
    return this.needsUpdate;
  }
}
