/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.core.client.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.CraftingProvider;
import top.theillusivec4.polymorph.core.Polymorph;

public class CraftingRecipeSelector
    extends AbstractRecipeSelector<CraftingInventory, CraftingRecipe> {

  private static Recipe<CraftingInventory> lastPlacedRecipe;
  private static List<CraftingRecipe> lastRecipesList;
  private static Recipe<CraftingInventory> lastSelectedRecipe;

  private boolean update = true;
  private boolean updatable = true;

  public static void update() {
    RecipeSelectorManager.getSelector().ifPresent(selector -> {
      if (selector instanceof CraftingRecipeSelector) {
        CraftingRecipeSelector craftingRecipeSelector = (CraftingRecipeSelector) selector;

        if (craftingRecipeSelector.updatable()) {
          craftingRecipeSelector.markUpdate();
        }
      }
    });
  }

  public static Optional<List<CraftingRecipe>> getLastRecipesList() {
    return Optional.ofNullable(lastRecipesList);
  }

  public static void setLastRecipesList(List<CraftingRecipe> recipesList) {
    lastRecipesList = recipesList;
  }

  public static Optional<Recipe<CraftingInventory>> getLastPlacedRecipe() {
    return Optional.ofNullable(lastPlacedRecipe);
  }

  public static void setLastPlacedRecipe(Recipe<CraftingInventory> recipe) {
    lastPlacedRecipe = recipe;
  }

  public static Optional<Recipe<CraftingInventory>> getLastSelectedRecipe() {
    return Optional.ofNullable(lastSelectedRecipe);
  }

  public static void setLastSelectedRecipe(Recipe<CraftingInventory> recipe) {
    lastSelectedRecipe = recipe;
  }

  public CraftingRecipeSelector(HandledScreen<?> screen, CraftingProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.update) {
      ClientWorld world = MinecraftClient.getInstance().world;
      this.update = false;

      if (world != null) {
        Optional<Recipe<CraftingInventory>> maybeLast = getLastPlacedRecipe();
        maybeLast.ifPresent(recipe -> {

          if (recipe.matches(this.provider.getInventory(), world)) {
            List<CraftingRecipe> recipes =
                getLastRecipesList().orElse(new ArrayList<>());
            this.setRecipes(recipes, world, false, "");
          } else {
            this.fetchRecipes();
          }
        });

        if (!maybeLast.isPresent()) {
          this.fetchRecipes();
        }
      }
    }
  }

  @Override
  public void setRecipes(List<CraftingRecipe> recipes, World world, boolean refresh,
                         String selected) {

    if (refresh) {
      Set<RecipeOutput> recipeOutputs = new HashSet<>();
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutput(rec.craft(this.provider.getInventory()))));

      if (!recipes.isEmpty()) {
        CraftingRecipe defaultRecipe = recipes.get(0);
        setLastSelectedRecipe(defaultRecipe);
        setLastPlacedRecipe(defaultRecipe);
        setLastRecipesList(recipes);
      }
    }
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;

    getLastSelectedRecipe().ifPresent(recipe -> {

      if (recipe.matches(provider.getInventory(), world)) {
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        this.updatable = false;

        if (playerEntity != null) {
          Polymorph.getLoader().getPacketVendor().sendSetCraftingRecipe(recipe.getId().toString());
        }
      }
    });

    RecipeSelectorManager.getPreferredRecipe().ifPresent(id -> {
      for (CraftingRecipe recipe : recipes) {

        if (recipe.getId() == id) {
          RecipeSelectorManager.setPreferredRecipe(null);
          selectRecipe(recipe);
          return;
        }
      }
    });
  }

  @Override
  public void selectRecipe(CraftingRecipe recipe) {
    setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;

    if (playerEntity != null) {
      ItemStack stack = recipe.craft(this.provider.getInventory());
      this.provider.getOutputSlot().setStack(stack.copy());
      Polymorph.getLoader().getPacketVendor().sendSetCraftingRecipe(recipe.getId().toString());
    }
  }

  @Override
  public void highlightRecipe(String recipe) {
    // NO-OP
  }

  @Override
  public void setRecipes(Set<String> recipeIds, World world, boolean refresh, String selected) {
    List<CraftingRecipe> recipes = new ArrayList<>();
    recipeIds.forEach(
        id -> world.getRecipeManager().get(new Identifier(id)).ifPresent(recipe -> {
          if (recipe instanceof CraftingRecipe) {
            recipes.add((CraftingRecipe) recipe);
          }
        }));
    recipes.sort(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()));
    this.setRecipes(recipes, world, false, selected);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (!super.mouseClicked(mouseX, mouseY, button)) {
      Slot slot = this.provider.getOutputSlot();
      Slot focusedSlot =
          Polymorph.getClientLoader().getClientAccessor().getFocusedSlot(this.parent);

      if (this.toggleButton.visible && slot == focusedSlot && isShiftKeyDown()) {
        return getLastSelectedRecipe().map(recipe -> {
          Polymorph.getLoader().getPacketVendor().sendTransferRecipe(recipe.getId().toString());
          return true;
        }).orElse(false);
      }
    }
    return false;
  }

  public boolean updatable() {
    return this.updatable;
  }

  public void setUpdatable(boolean flag) {
    this.updatable = flag;
  }

  public void markUpdate() {
    this.update = true;
  }

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  private static boolean isShiftKeyDown() {
    long handle = MinecraftClient.getInstance().getWindow().getHandle();
    return InputUtil.isKeyPressed(handle, GLFW_LEFT_SHIFT) || InputUtil
        .isKeyPressed(handle, GLFW_RIGHT_SHIFT);
  }
}
