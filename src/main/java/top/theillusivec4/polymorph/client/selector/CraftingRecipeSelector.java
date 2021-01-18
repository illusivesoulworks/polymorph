/*
 * Copyright (c) 2021 C4
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

package top.theillusivec4.polymorph.client.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.client.CPacketSetCraftingRecipe;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class CraftingRecipeSelector extends RecipeSelector<CraftingInventory, ICraftingRecipe> {

  private static IRecipe<CraftingInventory> lastPlacedRecipe;
  private static List<ICraftingRecipe> lastRecipesList;
  private static IRecipe<CraftingInventory> lastSelectedRecipe;

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

  public static Optional<List<ICraftingRecipe>> getLastRecipesList() {
    return Optional.ofNullable(lastRecipesList);
  }

  public static void setLastRecipesList(List<ICraftingRecipe> recipesList) {
    lastRecipesList = recipesList;
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

  public CraftingRecipeSelector(ContainerScreen<?> screen, ICraftingProvider provider) {
    super(screen, provider);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.update) {
      ClientWorld world = Minecraft.getInstance().world;
      this.update = false;

      if (world != null) {
        Optional<IRecipe<CraftingInventory>> maybeLast = getLastPlacedRecipe();
        maybeLast.ifPresent(recipe -> {

          if (recipe.matches(this.provider.getInventory(), world)) {
            List<ICraftingRecipe> recipes =
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
  public void setRecipes(List<ICraftingRecipe> recipes, World world, boolean refresh,
                         String selected) {

    if (refresh) {
      Set<RecipeOutput> recipeOutputs = new HashSet<>();
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutput(rec.getCraftingResult(this.provider.getInventory()))));

      if (!recipes.isEmpty()) {
        ICraftingRecipe defaultRecipe = recipes.get(0);
        setLastSelectedRecipe(defaultRecipe);
        setLastPlacedRecipe(defaultRecipe);
        setLastRecipesList(recipes);
      }
    } else {
      getLastSelectedRecipe().ifPresent(recipe -> {

        if (recipe.matches(provider.getInventory(), world)) {
          ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
          this.updatable = false;

          if (playerEntity != null) {
            NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                new CPacketSetCraftingRecipe(recipe.getId().toString()));
          }
        }
      });
    }
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;

    RecipeSelectorManager.getPreferredRecipe().ifPresent(id -> {
      for (ICraftingRecipe recipe : recipes) {

        if (recipe.getId() == id) {
          RecipeSelectorManager.setPreferredRecipe(null);
          selectRecipe(recipe);
          return;
        }
      }
    });
  }

  @Override
  public void selectRecipe(ICraftingRecipe recipe) {
    setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

    if (playerEntity != null) {
      ItemStack stack = recipe.getCraftingResult(this.provider.getInventory());
      this.provider.getOutputSlot().putStack(stack.copy());
      NetworkManager.INSTANCE
          .send(PacketDistributor.SERVER.noArg(),
              new CPacketSetCraftingRecipe(recipe.getId().toString()));
    }
  }

  @Override
  public void highlightRecipe(String recipe) {
    // NO-OP
  }

  @Override
  public void setRecipes(Set<String> recipeIds, World world, boolean refresh, String selected) {
    List<ICraftingRecipe> recipes = new ArrayList<>();
    recipeIds.forEach(
        id -> world.getRecipeManager().getRecipe(new ResourceLocation(id)).ifPresent(recipe -> {
          if (recipe instanceof ICraftingRecipe) {
            recipes.add((ICraftingRecipe) recipe);
          }
        }));
    recipes.sort(Comparator.comparing((recipe) -> recipe.getRecipeOutput().getTranslationKey()));
    this.setRecipes(recipes, world, refresh, selected);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (!super.mouseClicked(mouseX, mouseY, button)) {
      Slot slot = this.provider.getOutputSlot();

      if (this.toggleButton.visible && slot == this.parent.getSlotUnderMouse() &&
          isShiftKeyDown()) {
        return getLastSelectedRecipe().map(recipe -> {
          NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(),
              new CPacketTransferRecipe(recipe.getId().toString()));
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
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }
}
