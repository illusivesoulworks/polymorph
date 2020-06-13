package top.theillusivec4.polymorph.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;
import top.theillusivec4.polymorph.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class RecipeConflictManager<T extends Container> {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");

  private static RecipeConflictManager<?> instance;

  private RecipeSelectionGui recipeSelectionGui;

  private CraftingInventory currentCraftingMatrix;
  private IRecipe<CraftingInventory> lastPlacedRecipe;
  private List<ICraftingRecipe> lastRecipesList;
  private IRecipe<CraftingInventory> lastSelectedRecipe;

  private ImageButton switchButton;
  private boolean craftMatrixChanged;

  private ContainerScreen<T> parent;
  private IProvider<T> provider;

  public RecipeConflictManager(ContainerScreen<T> screen, IProvider<T> provider) {
    this.parent = screen;
    this.provider = provider;
    int x = screen.width / 2;
    int y = screen.height / 2;
    x += provider.getXOffset();
    y += provider.getYOffset();
    this.recipeSelectionGui = new RecipeSelectionGui(this, x - 4, y - 32);
    this.switchButton = new ImageButton(x, y, 16, 16, 0, 0, 17, SWITCH,
        clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
    this.switchButton.visible = this.recipeSelectionGui.getButtons().size() > 1;
    this.currentCraftingMatrix = provider.getCraftingMatrix(screen.getContainer());
  }

  public static Optional<RecipeConflictManager<?>> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static <T extends Container> RecipeConflictManager<?> refreshInstance(
      ContainerScreen<T> screen, IProvider<T> provider) {
    instance = new RecipeConflictManager<>(screen, provider);
    return instance;
  }

  public static void clearInstance() {
    instance = null;
  }

  public void tick() {

    if (this.craftMatrixChanged) {
      ClientWorld world = Minecraft.getInstance().world;
      this.craftMatrixChanged = false;

      if (world != null) {
        this.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {
          List<ICraftingRecipe> recipesList = this.getLastPlacedRecipe().map(recipe -> {
            if (recipe.matches(craftingInventory, world)) {
              return this.getLastRecipesList().orElse(new ArrayList<>());
            }
            return null;
          }).orElseGet(() -> this.fetchRecipes(craftingInventory, world));

          recipeSelectionGui.setRecipes(recipesList);

          this.getLastSelectedRecipe().ifPresent(recipe -> {

            if (recipe.matches(craftingInventory, world)) {
              ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

              if (playerEntity != null) {
                NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                    new CPacketSetRecipe(recipe.getId().toString()));
              }
            } else {
              this.setLastSelectedRecipe(null);
            }
          });
        });
      }
    }
  }

  private List<ICraftingRecipe> fetchRecipes(CraftingInventory craftingInventory, World world) {
    List<ICraftingRecipe> recipes = new ArrayList<>();
    boolean isCraftingEmpty = true;

    for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {

      if (!craftingInventory.getStackInSlot(i).isEmpty()) {
        isCraftingEmpty = false;
        break;
      }
    }

    if (!isCraftingEmpty) {
      Polymorph.LOGGER.info("fetching new recipes");
      Set<RecipeOutputWrapper> recipeOutputs = new HashSet<>();
      recipes = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, craftingInventory, world);
      recipes.removeIf(rec -> !recipeOutputs.add(new RecipeOutputWrapper(rec.getCraftingResult(craftingInventory))));

      if (!recipes.isEmpty()) {
        this.setLastSelectedRecipe(null);
        this.setLastPlacedRecipe(recipes.get(0));
        this.setLastRecipesList(recipes);
      }
    }
    return recipes;
  }

  public void renderRecipeSelectionGui(int mouseX, int mouseY, float partialTicks) {
    this.recipeSelectionGui.render(mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.recipeSelectionGui.mouseClicked(mouseX, mouseY, button)) {
      this.recipeSelectionGui.setVisible(false);
      return true;
    } else if (this.recipeSelectionGui.isVisible()) {

      if (!this.switchButton.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectionGui.setVisible(false);
      }
      return true;
    }
    Slot slot = this.provider.getOutputSlot(this.parent.getContainer());

    if (slot == this.parent.getSlotUnderMouse() && isShiftKeyDown()) {
      return this.getLastSelectedRecipe()
          .map(recipe -> this.getCurrentCraftingMatrix().map(craftingInventory -> {
            NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                new CPacketTransferRecipe(recipe.getId().toString()));
            return true;
          }).orElse(false)).orElse(false);
    }
    return false;
  }

  public void selectRecipe(IRecipe<CraftingInventory> recipe) {
    this.setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

    if (playerEntity != null) {
      this.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {
        ItemStack stack = recipe.getCraftingResult(craftingInventory);
        this.provider.getOutputSlot(parent.getContainer()).putStack(stack.copy());
        NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
            new CPacketSetRecipe(recipe.getId().toString()));
      });
    }
  }

  public Optional<List<ICraftingRecipe>> getLastRecipesList() {
    return Optional.ofNullable(lastRecipesList);
  }

  public void setLastRecipesList(List<ICraftingRecipe> recipesList) {
    lastRecipesList = recipesList;
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

  public ImageButton getSwitchButton() {
    return this.switchButton;
  }

  public void onCraftMatrixChanged() {
    this.craftMatrixChanged = true;
  }

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  private static boolean isShiftKeyDown() {
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }
}
