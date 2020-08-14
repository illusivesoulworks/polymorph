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

package top.theillusivec4.polymorph.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.client.gui.ToggleRecipeButton;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.client.CPacketFetchRecipes;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class RecipeSelectionManager {

  private static final ResourceLocation TOGGLE = new ResourceLocation(Polymorph.MODID,
      "textures/gui/toggle.png");
  private static final Field RECIPE_BOOK = ObfuscationReflectionHelper
      .findField(RecipeBookGui.class, "field_193964_s");

  private static RecipeSelectionManager instance;
  private static ItemStack preferredStack = ItemStack.EMPTY;

  private RecipeSelectionGui recipeSelectionGui;
  private ImageButton toggleButton;

  private CraftingInventory craftingInventory;

  private IRecipe<CraftingInventory> lastPlacedRecipe;
  private List<ICraftingRecipe> lastRecipesList;
  private IRecipe<CraftingInventory> lastSelectedRecipe;

  private boolean needsUpdate;
  private boolean canUpdate = true;
  private boolean needsPositionUpdate;

  private ContainerScreen<?> parent;
  private PolyProvider provider;

  public static Optional<RecipeSelectionManager> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static RecipeSelectionManager createInstance(ContainerScreen<?> screen,
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

  public RecipeSelectionManager(ContainerScreen<?> screen, PolyProvider provider) {
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

  public void tick() {

    if (this.needsPositionUpdate) {
      this.needsPositionUpdate = false;
      int x = this.parent.width / 2 + provider.getXOffset();
      int y = this.parent.height / 2 + provider.getYOffset();

      if (this.parent instanceof IRecipeShownListener) {
        IRecipeShownListener recipeShownListener = (IRecipeShownListener) this.parent;
        RecipeBookGui recipeBookGui = recipeShownListener.getRecipeGui();
        RecipeBook recipeBook = null;

        try {
          recipeBook = (RecipeBook) RECIPE_BOOK.get(recipeBookGui);
        } catch (IllegalAccessException e) {
          Polymorph.LOGGER.error("Something went wrong while accessing recipe book!");
        }

        if (recipeBook != null && recipeBookGui.isVisible()) {
          x += 77;
        }
      }
      this.recipeSelectionGui.setPosition(x - 4, y - 32);
      this.toggleButton.setPosition(x, y);
    }

    if (this.needsUpdate) {
      ClientWorld world = Minecraft.getInstance().world;
      this.needsUpdate = false;

      if (world != null) {
        this.getLastPlacedRecipe().ifPresent(recipe -> {

          if (recipe.matches(craftingInventory, world)) {
            List<ICraftingRecipe> recipes = this.getLastRecipesList().orElse(new ArrayList<>());
            this.setRecipes(recipes, world, false);
          } else {
            this.fetchRecipes();
          }
        });

        if (!this.getLastPlacedRecipe().isPresent()) {
          this.fetchRecipes();
        }
      }
    }
  }

  public void setRecipes(List<ICraftingRecipe> recipes, World world, boolean refresh) {

    if (refresh) {
      Set<RecipeOutputWrapper> recipeOutputs = new HashSet<>();
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutputWrapper(rec.getCraftingResult(craftingInventory))));

      if (!recipes.isEmpty()) {
        ICraftingRecipe defaultRecipe = recipes.get(0);
        this.setLastSelectedRecipe(defaultRecipe);
        this.setLastPlacedRecipe(defaultRecipe);
        this.setLastRecipesList(recipes);
      }
    }
    this.recipeSelectionGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;

    if (!preferredStack.isEmpty()) {

      for (ICraftingRecipe craftingRecipe : recipes) {

        if (craftingRecipe.getCraftingResult(craftingInventory).getItem() == preferredStack
            .getItem()) {
          this.setLastSelectedRecipe(craftingRecipe);
          break;
        }
      }
      preferredStack = ItemStack.EMPTY;
    }

    this.getLastSelectedRecipe().ifPresent(recipe -> {

      if (recipe.matches(craftingInventory, world)) {
        ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
        this.lockUpdates();

        if (playerEntity != null) {
          NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
              new CPacketSetRecipe(recipe.getId().toString()));
        }
      }
    });
  }

  private void fetchRecipes() {
    NetworkHandler.INSTANCE.sendToServer(new CPacketFetchRecipes());
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

    if (this.toggleButton.visible && slot == this.parent.getSlotUnderMouse() && isShiftKeyDown()) {
      return this.getLastSelectedRecipe().map(recipe -> {
        NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
            new CPacketTransferRecipe(recipe.getId().toString()));
        return true;
      }).orElse(false);
    }
    return false;
  }

  public void selectRecipe(IRecipe<CraftingInventory> recipe) {
    this.setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

    if (playerEntity != null) {
      ItemStack stack = recipe.getCraftingResult(craftingInventory);
      this.provider.getOutputSlot().putStack(stack.copy());
      NetworkHandler.INSTANCE
          .send(PacketDistributor.SERVER.noArg(), new CPacketSetRecipe(recipe.getId().toString()));
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
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }
}
