/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.core.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.core.Polymorph;
import top.theillusivec4.polymorph.core.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.core.client.gui.ToggleRecipeButton;

public class RecipeSelectionManager {

  private static final Identifier TOGGLE = new Identifier(Polymorph.MODID,
      "textures/gui/toggle.png");

  private static RecipeSelectionManager instance;
  private static ItemStack preferredStack = ItemStack.EMPTY;

  private RecipeSelectionGui recipeSelectionGui;
  private TexturedButtonWidget toggleButton;

  private CraftingInventory craftingInventory;

  private Recipe<CraftingInventory> lastPlacedRecipe;
  private List<CraftingRecipe> lastRecipesList;
  private Recipe<CraftingInventory> lastSelectedRecipe;

  private boolean needsUpdate;
  private boolean canUpdate = true;
  private boolean needsPositionUpdate;

  private HandledScreen<?> parent;
  private PolyProvider provider;

  public static Optional<RecipeSelectionManager> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static RecipeSelectionManager createInstance(HandledScreen<?> screen,
      PolyProvider provider) {
    instance = new RecipeSelectionManager(screen, provider);
    return instance;
  }

  public static void clearInstance() {
    instance = null;
  }

  public static ItemStack getPreferredStack() {
    return preferredStack;
  }

  public static void setPreferredStack(ItemStack stack) {
    preferredStack = stack.copy();
  }

  public static void updateManager() {
    RecipeSelectionManager.getInstance().ifPresent(manager -> {

      if (manager.canUpdate) {
        manager.markUpdate();
      }
    });
  }

  public RecipeSelectionManager(HandledScreen<?> screen, PolyProvider provider) {
    this.parent = screen;
    this.provider = provider;
    int x = screen.width / 2 + provider.getXOffset();
    int y = screen.height / 2 + provider.getYOffset();
    this.craftingInventory = provider.getCraftingInventory();
    this.recipeSelectionGui = new RecipeSelectionGui(x - 4, y - 32,
        Objects.requireNonNull(this.craftingInventory), this::selectRecipe);
    this.toggleButton = new ToggleRecipeButton(x, y, 16, 16, 0, 0, 17, TOGGLE,
        clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
    this.toggleButton.visible = this.recipeSelectionGui.getButtons().size() > 1;
  }

  public Optional<List<CraftingRecipe>> getLastRecipesList() {
    return Optional.ofNullable(lastRecipesList);
  }

  public void setLastRecipesList(List<CraftingRecipe> recipesList) {
    lastRecipesList = recipesList;
  }

  public Optional<Recipe<CraftingInventory>> getLastPlacedRecipe() {
    return Optional.ofNullable(lastPlacedRecipe);
  }

  public void setLastPlacedRecipe(Recipe<CraftingInventory> recipe) {
    lastPlacedRecipe = recipe;
  }

  public Optional<Recipe<CraftingInventory>> getLastSelectedRecipe() {
    return Optional.ofNullable(lastSelectedRecipe);
  }

  public void setLastSelectedRecipe(Recipe<CraftingInventory> recipe) {
    lastSelectedRecipe = recipe;
  }

  public void tick() {

    if (this.needsPositionUpdate) {
      this.needsPositionUpdate = false;
      int x = this.parent.width / 2 + provider.getXOffset();
      int y = this.parent.height / 2 + provider.getYOffset();

      if (this.parent instanceof RecipeBookProvider) {
        RecipeBookProvider recipeShownListener = (RecipeBookProvider) this.parent;
        RecipeBookWidget recipeBookGui = recipeShownListener.getRecipeBookWidget();
        RecipeBook recipeBook = Polymorph.getClientLoader().getClientAccessor()
            .getRecipeBook(recipeBookGui);

        if (recipeBook != null && recipeBookGui.isOpen()) {
          x += 77;
        }
      }
      this.recipeSelectionGui.setPosition(x - 4, y - 32);
      this.toggleButton.setPos(x, y);
    }

    if (this.needsUpdate) {
      ClientWorld world = MinecraftClient.getInstance().world;
      this.needsUpdate = false;

      if (world != null) {
        List<CraftingRecipe> recipesList = this.getLastPlacedRecipe().map(recipe -> {
          if (recipe.matches(craftingInventory, world)) {
            return this.getLastRecipesList().orElse(new ArrayList<>());
          }
          return null;
        }).orElseGet(() -> this.fetchRecipes(craftingInventory, world));

        this.recipeSelectionGui.setRecipes(recipesList);
        this.toggleButton.visible = recipesList.size() > 1;

        if (!preferredStack.isEmpty()) {

          for (CraftingRecipe craftingRecipe : recipesList) {

            if (craftingRecipe.craft(craftingInventory).getItem() == preferredStack.getItem()) {
              this.setLastSelectedRecipe(craftingRecipe);
              break;
            }
          }
          preferredStack = ItemStack.EMPTY;
        }

        this.getLastSelectedRecipe().ifPresent(recipe -> {

          if (recipe.matches(craftingInventory, world)) {
            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            this.lockUpdates();

            if (playerEntity != null) {
              Polymorph.getLoader().getPacketVendor().sendSetRecipe(recipe.getId().toString());
            }
          }
        });
      }
    }
  }

  private List<CraftingRecipe> fetchRecipes(CraftingInventory craftingInventory, World world) {
    List<CraftingRecipe> recipes = new ArrayList<>();
    boolean isCraftingEmpty = true;

    for (int i = 0; i < craftingInventory.size(); i++) {

      if (!craftingInventory.getStack(i).isEmpty()) {
        isCraftingEmpty = false;
        break;
      }
    }

    if (!isCraftingEmpty) {
      Set<RecipeOutputWrapper> recipeOutputs = new HashSet<>();

      try {
        recipes = world.getRecipeManager()
            .getAllMatches(RecipeType.CRAFTING, craftingInventory, world);
      } catch (Exception e) {
        List<String> stacks = new ArrayList<>();

        for (int i = 0; i < craftingInventory.size(); i++) {
          stacks.add(craftingInventory.getStack(i).toString());
        }
        Polymorph.LOGGER.error("Attempted to craft using " + Arrays.toString(stacks.toArray())
            + " but an error occurred while fetching recipes!", e);
      }
      recipes.removeIf(
          rec -> !recipeOutputs.add(new RecipeOutputWrapper(rec.craft(craftingInventory))));

      if (!recipes.isEmpty()) {
        CraftingRecipe defaultRecipe = recipes.get(0);
        this.setLastSelectedRecipe(defaultRecipe);
        this.setLastPlacedRecipe(defaultRecipe);
        this.setLastRecipesList(recipes);
      }
    }
    return recipes;
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.recipeSelectionGui.render(matrixStack, mouseX, mouseY, partialTicks);
    this.toggleButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.recipeSelectionGui.mouseClicked(mouseX, mouseY, button)) {
      this.recipeSelectionGui.setVisible(false);
      return true;
    } else if (this.recipeSelectionGui.isVisible()) {

      if (!this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectionGui.setVisible(false);
      }
      return true;
    }
    Slot slot = this.provider.getOutputSlot();
    Slot focusedSlot = Polymorph.getClientLoader().getClientAccessor().getFocusedSlot(this.parent);

    if (this.toggleButton.visible && slot == focusedSlot && isShiftKeyDown()) {
      return this.getLastSelectedRecipe().map(recipe -> {
        Polymorph.getLoader().getPacketVendor().sendTransferRecipe(recipe.getId().toString());
        return true;
      }).orElse(false);
    }
    return false;
  }

  public void selectRecipe(Recipe<CraftingInventory> recipe) {
    this.setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;

    if (playerEntity != null) {
      ItemStack stack = recipe.craft(craftingInventory);
      this.provider.getOutputSlot().setStack(stack.copy());
      Polymorph.getLoader().getPacketVendor().sendSetRecipe(recipe.getId().toString());
    }
  }

  public void markPositionChanged() {
    this.needsPositionUpdate = true;
  }

  public void lockUpdates() {
    this.canUpdate = false;
  }

  public void unlockUpdates() {
    this.canUpdate = true;
  }

  public void markUpdate() {
    this.needsUpdate = true;
  }

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  private static boolean isShiftKeyDown() {
    long handle = MinecraftClient.getInstance().getWindow().getHandle();
    return InputUtil.isKeyPressed(handle, GLFW_LEFT_SHIFT) || InputUtil
        .isKeyPressed(handle, GLFW_RIGHT_SHIFT);
  }
}
