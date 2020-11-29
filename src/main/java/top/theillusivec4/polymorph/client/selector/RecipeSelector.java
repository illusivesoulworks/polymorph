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

package top.theillusivec4.polymorph.client.selector;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.type.IPolyProvider;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;
import top.theillusivec4.polymorph.client.gui.RecipeSelectorGui;
import top.theillusivec4.polymorph.client.gui.ToggleRecipeButton;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.client.CPacketFetchRecipes;

public abstract class RecipeSelector<I extends IInventory, P extends IPolyProvider<I>, R extends IRecipe<I>>
    implements IRecipeSelector<I, R> {

  private static final ResourceLocation TOGGLE = new ResourceLocation(Polymorph.MODID,
      "textures/gui/toggle.png");
  private static final int SELECTOR_X_OFFSET = -4;
  private static final int SELECTOR_Y_OFFSET = -26;

  protected final RecipeSelectorGui<I, R> recipeSelectorGui;
  protected final ImageButton toggleButton;
  protected final P provider;
  protected final ContainerScreen<?> parent;

  private boolean updatePosition = false;

  public RecipeSelector(ContainerScreen<?> screen, P provider) {
    this.parent = screen;
    this.provider = provider;
    int x = screen.getGuiLeft() + provider.getXPos();
    int y = screen.getGuiTop() + provider.getYPos();
    this.recipeSelectorGui =
        new RecipeSelectorGui<>(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET,
            provider.getInventory(), this::selectRecipe, this.parent);
    this.toggleButton = new ToggleRecipeButton(x, y, 16, 16, 0, 0, 17, TOGGLE,
        clickWidget -> recipeSelectorGui.setVisible(!recipeSelectorGui.isVisible()));
    this.toggleButton.visible = this.recipeSelectorGui.getButtons().size() > 1;
  }

  public void reposition() {
    int x = this.parent.getGuiLeft() + provider.getXPos();
    int y = this.parent.getGuiTop() + provider.getYPos();
    this.recipeSelectorGui.setPosition(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET);
    this.toggleButton.setPosition(x, y);
  }

  public void tick() {

    if (this.updatePosition) {
      this.updatePosition = false;
      this.reposition();
    }
  }

  public void clearRecipes(World world) {
    this.setRecipes(new ArrayList<>(), world, false);
  }

  protected void fetchRecipes() {
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
    return false;
  }

  public void markUpdatePosition() {
    this.updatePosition = true;
  }

  static class RecipeOutput {

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
