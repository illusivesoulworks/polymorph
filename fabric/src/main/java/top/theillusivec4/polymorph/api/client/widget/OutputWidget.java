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

package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.common.base.RecipePair;

public class OutputWidget extends ClickableWidget {

  private final ItemStack output;
  private final Identifier identifier;

  public boolean highlighted = false;

  public OutputWidget(RecipePair pRecipeData) {
    super(0, 0, 25, 25, LiteralText.EMPTY);
    this.output = pRecipeData.getOutput();
    this.identifier = pRecipeData.getIdentifier();
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int button_1, int button_2, float button_3) {
    MinecraftClient minecraft = MinecraftClient.getInstance();
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, AbstractRecipesWidget.WIDGETS);
    int j = 0;

    if (this.x + 25 > button_1 && this.x <= button_1 && this.y + 25 > button_2 &&
        this.y <= button_2) {
      j += 25;
    }
    this.drawTexture(matrixStack, this.x, this.y, this.highlighted ? 41 : 16, j, this.width,
        this.height);
    int k = 4;
    ItemRenderer renderer = minecraft.getItemRenderer();
    float zLevel = renderer.zOffset;
    renderer.zOffset = 700.0F;
    renderer.renderGuiItemIcon(this.getOutput(), this.x + k, this.y + k);
    renderer.renderGuiItemOverlay(minecraft.textRenderer, this.getOutput(), this.x + k, this.y + k);
    renderer.zOffset = zLevel;
  }

  public void setHighlighted(boolean pHighlighted) {
    this.highlighted = pHighlighted;
  }

  public ItemStack getOutput() {
    return this.output;
  }

  public Identifier getIdentifier() {
    return this.identifier;
  }

  public List<Text> getTooltipText(Screen pScreen) {
    return pScreen.getTooltipFromItem(this.getOutput());
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected boolean isValidClickButton(int button) {
    return button == 0 || button == 1;
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    this.appendDefaultNarrations(builder);
  }
}