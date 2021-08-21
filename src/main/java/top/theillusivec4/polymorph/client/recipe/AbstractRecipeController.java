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

package top.theillusivec4.polymorph.client.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.RecipeController;
import top.theillusivec4.polymorph.client.gui.RecipeSelectorWidget;
import top.theillusivec4.polymorph.client.gui.ToggleSelectorButton;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

public abstract class AbstractRecipeController<I extends Inventory, R extends Recipe<I>>
    implements RecipeController<I, R> {

  public static final Identifier TOGGLE = new Identifier(PolymorphMod.MOD_ID,
      "textures/gui/toggle.png");

  private static final int SELECTOR_X_OFFSET = -4;
  private static final int SELECTOR_Y_OFFSET = -26;

  protected RecipeSelectorWidget<I, R> recipeSelectorWidget;
  protected ButtonWidget toggleButton;
  protected final HandledScreen<?> parentScreen;

  public AbstractRecipeController(HandledScreen<?> screen) {
    this.parentScreen = screen;
  }

  protected void init() {
    int x = ((AccessorHandledScreen) this.parentScreen).getX() + this.getXPos();
    int y = ((AccessorHandledScreen) this.parentScreen).getY() + this.getYPos();
    this.recipeSelectorWidget =
        new RecipeSelectorWidget<>(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET,
            this.getInventory(), this::selectRecipe, this.parentScreen);
    this.toggleButton = new ToggleSelectorButton(x, y, 16, 16, 0, 0, 17, TOGGLE,
        clickWidget -> recipeSelectorWidget.setActive(!recipeSelectorWidget.isActive()));
    this.toggleButton.visible = this.recipeSelectorWidget.getOutputWidgets().size() > 1;
  }

  @Override
  public void selectRecipe(R recipe) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(recipe.getId());
    ClientPlayNetworking.send(PolymorphPackets.SELECT_CRAFT, buf);
  }

  @Override
  public void highlightRecipe(String recipe) {
    this.recipeSelectorWidget.highlightButton(recipe);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setRecipes(Set<Identifier> recipes, World world, Identifier selected) {
    List<R> list = new ArrayList<>();

    for (Identifier recipe : recipes) {
      world.getRecipeManager().get(recipe).ifPresent(result -> list.add((R) result));
    }
    Set<RecipeOutput> recipeOutputs = new HashSet<>();
    list.removeIf(rec -> !recipeOutputs
        .add(new RecipeOutput(rec.craft(this.getInventory()))));
    this.recipeSelectorWidget.setRecipes(list);
    this.toggleButton.visible = recipes.size() > 1;

    if (selected != null) {
      this.highlightRecipe(selected.toString());
    }
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.recipeSelectorWidget.render(matrixStack, mouseX, mouseY, partialTicks);
    this.toggleButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.recipeSelectorWidget.mouseClicked(mouseX, mouseY, button)) {
      this.recipeSelectorWidget.setActive(false);
      return true;
    } else if (this.recipeSelectorWidget.isActive()) {

      if (!this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectorWidget.setActive(false);
      }
      return true;
    }
    return false;
  }

  static class RecipeOutput {

    private final Item item;
    private final int count;
    private final NbtCompound tag;

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
