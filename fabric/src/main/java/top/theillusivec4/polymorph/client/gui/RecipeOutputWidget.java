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

package top.theillusivec4.polymorph.client.gui;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class RecipeOutputWidget<T extends Inventory, R extends Recipe<T>> extends ClickableWidget {

  private static final Identifier TOGGLE =
      new Identifier(PolymorphMod.MOD_ID, "textures/gui/toggle.png");

  private final ItemStack output;
  private final R recipe;

  public boolean highlighted = false;

  public RecipeOutputWidget(R recipe, ItemStack output) {
    super(0, 0, 25, 25, LiteralText.EMPTY);
    this.output = output;
    this.recipe = recipe;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int button_1, int button_2, float button_3) {
    MinecraftClient minecraft = MinecraftClient.getInstance();
    minecraft.getTextureManager().bindTexture(TOGGLE);
    int j = 0;

    if (this.x + 25 > button_1 && this.x <= button_1 && this.y + 25 > button_2 &&
        this.y <= button_2) {
      j += 25;
    }
    this.drawTexture(matrixStack, this.x, this.y, this.highlighted ? 41 : 16, j, this.width,
        this.height);
    int k = 4;
    float zLevel = minecraft.getItemRenderer().zOffset;
    minecraft.getItemRenderer().zOffset = 600.0F;
    minecraft.getItemRenderer().renderGuiItemIcon(this.getOutput(), this.x + k, this.y + k);
    minecraft.getItemRenderer().zOffset = zLevel;
  }

  public ItemStack getOutput() {
    return this.output;
  }

  public R getRecipe() {
    return this.recipe;
  }

  public List<Text> getTooltipText(Screen screen) {
    return screen.getTooltipFromItem(this.getOutput());
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected boolean isValidClickButton(int button) {
    return button == 0 || button == 1;
  }
}