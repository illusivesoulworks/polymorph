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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;
import top.theillusivec4.polymorph.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.client.gui.SwitchButton;
import top.theillusivec4.polymorph.common.integrations.jei.PolymorphJeiPlugin;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class RecipeConflictManager {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");
  private static final Field RECIPE_BOOK = ObfuscationReflectionHelper
      .findField(RecipeBookGui.class, "field_193964_s");

  private static RecipeConflictManager instance;

  private RecipeSelectionGui recipeSelectionGui;

  private CraftingInventory currentCraftingMatrix;
  private IRecipe<CraftingInventory> lastPlacedRecipe;
  private List<ICraftingRecipe> lastRecipesList;
  private IRecipe<CraftingInventory> lastSelectedRecipe;

  private ImageButton switchButton;
  private boolean craftMatrixChanged;
  private boolean positionChanged;

  private ContainerScreen<?> parent;
  private IProvider provider;

  public RecipeConflictManager(ContainerScreen<?> screen, IProvider provider) {
    this.parent = screen;
    this.provider = provider;
    int x = screen.width / 2;
    int y = screen.height / 2;
    x += provider.getXOffset();
    y += provider.getYOffset();
    this.recipeSelectionGui = new RecipeSelectionGui(this, x - 4, y - 32);
    this.switchButton = new SwitchButton(x, y, 16, 16, 0, 0, 17, SWITCH,
        clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
    this.switchButton.visible = this.recipeSelectionGui.getButtons().size() > 1;
    this.currentCraftingMatrix = provider.getCraftingInventory();
  }

  public static Optional<RecipeConflictManager> getInstance() {
    return Optional.ofNullable(instance);
  }

  public static RecipeConflictManager refreshInstance(ContainerScreen<?> screen,
      IProvider provider) {
    instance = new RecipeConflictManager(screen, provider);
    return instance;
  }

  public static void clearInstance() {
    instance = null;
  }

  public void updatePosition() {
    this.positionChanged = true;
  }

  public void tick() {

    if (this.positionChanged) {
      this.positionChanged = false;
      int x = this.parent.width / 2;
      int y = this.parent.height / 2;
      x += provider.getXOffset();
      y += provider.getYOffset();

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
      this.switchButton.setPosition(x, y);
    }

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

          if (Polymorph.isJeiLoaded) {
            ItemStack chosenStack = PolymorphJeiPlugin.getItemStack();

            if (!chosenStack.isEmpty()) {

              for (ICraftingRecipe craftingRecipe : recipesList) {

                if (craftingRecipe.getCraftingResult(craftingInventory).getItem() == chosenStack
                    .getItem()) {
                  this.setLastSelectedRecipe(craftingRecipe);
                  break;
                }
              }
            }
            PolymorphJeiPlugin.clearItemStack();
          }

          this.getLastSelectedRecipe().ifPresent(recipe -> {

            if (recipe.matches(craftingInventory, world)) {
              ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

              if (playerEntity != null) {
                NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                    new CPacketSetRecipe(recipe.getId().toString()));
              }
            }
          });
        });
      }
    }
  }

  private synchronized List<ICraftingRecipe> fetchRecipes(CraftingInventory craftingInventory,
      World world) {
    List<ICraftingRecipe> recipes = new ArrayList<>();
    boolean isCraftingEmpty = true;

    for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {

      if (!craftingInventory.getStackInSlot(i).isEmpty()) {
        isCraftingEmpty = false;
        break;
      }
    }

    if (!isCraftingEmpty) {
      //      Polymorph.LOGGER.info("fetching new recipes");
      Set<RecipeOutputWrapper> recipeOutputs = new HashSet<>();
      try {
        synchronized (this) {
          recipes = world.getRecipeManager()
              .getRecipes(IRecipeType.CRAFTING, craftingInventory, world);
        }
      } catch (Exception e) {
        List<String> stacks = new ArrayList<>();

        for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {
          stacks.add(craftingInventory.getStackInSlot(i).toString());
        }
        Polymorph.LOGGER.error("Attempted to craft using " + Arrays.toString(stacks.toArray())
            + " but an error occurred while fetching recipes!", e);
      }
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutputWrapper(rec.getCraftingResult(craftingInventory))));

      if (!recipes.isEmpty()) {
        ICraftingRecipe defaultRecipe = recipes.get(0);
        this.setLastSelectedRecipe(defaultRecipe);
        this.setLastPlacedRecipe(defaultRecipe);
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
    Slot slot = this.provider.getOutputSlot();

    if (this.getSwitchButton().visible && slot == this.parent.getSlotUnderMouse()
        && isShiftKeyDown()) {
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
        this.provider.getOutputSlot().putStack(stack.copy());
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
