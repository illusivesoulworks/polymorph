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
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.client.gui.RecipeSelectorGui;
import top.theillusivec4.polymorph.client.gui.ToggleRecipeButton;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.client.CPacketFetchRecipes;
import top.theillusivec4.polymorph.common.network.client.CPacketSetRecipe;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class RecipeSelector {

  private static final ResourceLocation TOGGLE = new ResourceLocation(Polymorph.MODID,
      "textures/gui/toggle.png");
  private static final Field RECIPE_BOOK = ObfuscationReflectionHelper
      .findField(RecipeBookGui.class, "field_193964_s");
  private static final int SELECTOR_X_OFFSET = -4;
  private static final int SELECTOR_Y_OFFSET = -26;

  private final RecipeSelectorGui recipeSelectorGui;
  private final ImageButton toggleButton;
  private final PolyProvider provider;
  private final ContainerScreen<?> parent;

  private boolean update = true;
  private boolean updatable = true;
  private boolean updatePosition = false;

  RecipeSelector(ContainerScreen<?> screen, PolyProvider provider) {
    this.parent = screen;
    this.provider = provider;
    int x = screen.getGuiLeft() + provider.getXPos();
    int y = screen.getGuiTop() + provider.getYPos();
    this.recipeSelectorGui =
        new RecipeSelectorGui(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET,
            provider.getCraftingInventory(), this::selectRecipe, this.parent);
    this.toggleButton = new ToggleRecipeButton(x, y, 16, 16, 0, 0, 17, TOGGLE,
        clickWidget -> recipeSelectorGui.setVisible(!recipeSelectorGui.isVisible()));
    this.toggleButton.visible = this.recipeSelectorGui.getButtons().size() > 1;
  }

  public void tick() {

    if (this.updatePosition) {
      this.updatePosition = false;
      int x = this.parent.getGuiLeft() + provider.getXPos();
      int y = this.parent.getGuiTop() + provider.getYPos();
      this.recipeSelectorGui.setPosition(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET);
      this.toggleButton.setPosition(x, y);
    }

    if (this.update) {
      ClientWorld world = Minecraft.getInstance().world;
      this.update = false;

      if (world != null) {
        RecipeSelectorManager.getLastPlacedRecipe().ifPresent(recipe -> {

          if (recipe.matches(this.provider.getCraftingInventory(), world)) {
            List<ICraftingRecipe> recipes =
                RecipeSelectorManager.getLastRecipesList().orElse(new ArrayList<>());
            this.setRecipes(recipes, world, false);
          } else {
            this.fetchRecipes();
          }
        });

        if (!RecipeSelectorManager.getLastPlacedRecipe().isPresent()) {
          this.fetchRecipes();
        }
      }
    }
  }

  public void clearRecipes(World world) {
    this.setRecipes(new ArrayList<>(), world, false);
  }

  public void setRecipes(List<ICraftingRecipe> recipes, World world, boolean refresh) {

    if (refresh) {
      Set<RecipeOutput> recipeOutputs = new HashSet<>();
      recipes.removeIf(rec -> !recipeOutputs
          .add(new RecipeOutput(rec.getCraftingResult(this.provider.getCraftingInventory()))));

      if (!recipes.isEmpty()) {
        ICraftingRecipe defaultRecipe = recipes.get(0);
        RecipeSelectorManager.setLastSelectedRecipe(defaultRecipe);
        RecipeSelectorManager.setLastPlacedRecipe(defaultRecipe);
        RecipeSelectorManager.setLastRecipesList(recipes);
      }
    }
    this.recipeSelectorGui.setRecipes(recipes);
    this.toggleButton.visible = recipes.size() > 1;
    ItemStack stack = RecipeSelectorManager.getPreferredStack();

    if (!stack.isEmpty()) {

      for (ICraftingRecipe craftingRecipe : recipes) {

        if (craftingRecipe.getCraftingResult(this.provider.getCraftingInventory()).getItem() ==
            stack.getItem()) {
          RecipeSelectorManager.setLastSelectedRecipe(craftingRecipe);
          break;
        }
      }
      RecipeSelectorManager.setPreferredStack(ItemStack.EMPTY);
    }

    RecipeSelectorManager.getLastSelectedRecipe().ifPresent(recipe -> {

      if (recipe.matches(provider.getCraftingInventory(), world)) {
        ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
        this.updatable = false;

        if (playerEntity != null) {
          NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(),
              new CPacketSetRecipe(recipe.getId().toString()));
        }
      }
    });
  }

  private void fetchRecipes() {
    NetworkManager.INSTANCE.sendToServer(new CPacketFetchRecipes());
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.recipeSelectorGui.render(matrixStack, mouseX, mouseY, partialTicks);
    this.toggleButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.recipeSelectorGui.mouseClicked(mouseX, mouseY, button)) {
      this.recipeSelectorGui.setVisible(false);
      return true;
    } else if (this.recipeSelectorGui.isVisible()) {

      if (!this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectorGui.setVisible(false);
      }
      return true;
    }
    Slot slot = this.provider.getOutputSlot();

    if (this.toggleButton.visible && slot == this.parent.getSlotUnderMouse() && isShiftKeyDown()) {
      return RecipeSelectorManager.getLastSelectedRecipe().map(recipe -> {
        NetworkManager.INSTANCE.send(PacketDistributor.SERVER.noArg(),
            new CPacketTransferRecipe(recipe.getId().toString()));
        return true;
      }).orElse(false);
    }
    return false;
  }

  public void selectRecipe(IRecipe<CraftingInventory> recipe) {
    RecipeSelectorManager.setLastSelectedRecipe(recipe);
    ClientPlayerEntity playerEntity = Minecraft.getInstance().player;

    if (playerEntity != null) {
      ItemStack stack = recipe.getCraftingResult(this.provider.getCraftingInventory());
      this.provider.getOutputSlot().putStack(stack.copy());
      NetworkManager.INSTANCE
          .send(PacketDistributor.SERVER.noArg(), new CPacketSetRecipe(recipe.getId().toString()));
    }
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

  public void markUpdatePosition() {
    this.updatePosition = true;
  }

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  private static boolean isShiftKeyDown() {
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }

  private static class RecipeOutput {

    private final Item item;
    private final int count;
    private final CompoundNBT tag;

    public RecipeOutput(ItemStack stack) {
      this.item = stack.getItem();
      this.count = stack.getCount();
      this.tag = stack.getTag();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      RecipeOutput that = (RecipeOutput) o;
      return count == that.count && item.equals(that.item) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
      return Objects.hash(item, count, tag);
    }
  }
}
