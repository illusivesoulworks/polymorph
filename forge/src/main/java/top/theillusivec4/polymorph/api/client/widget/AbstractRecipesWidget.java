package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

public abstract class AbstractRecipesWidget implements IRecipesWidget {

  public static final ResourceLocation WIDGETS =
      new ResourceLocation(PolymorphApi.MOD_ID, "textures/gui/widgets.png");
  public static final int BUTTON_X_OFFSET = 0;
  public static final int BUTTON_Y_OFFSET = -22;
  public static final int WIDGET_X_OFFSET = -4;
  public static final int WIDGET_Y_OFFSET = -26;

  protected final ContainerScreen<?> containerScreen;
  protected final int xOffset;
  protected final int yOffset;

  protected SelectionWidget selectionWidget;
  protected Button openButton;

  public AbstractRecipesWidget(ContainerScreen<?> containerScreen, int xOffset, int yOffset) {
    this.containerScreen = containerScreen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  public AbstractRecipesWidget(ContainerScreen<?> containerScreen) {
    this(containerScreen, WIDGET_X_OFFSET, WIDGET_Y_OFFSET);
  }

  @Override
  public void initChildWidgets() {
    int x = this.containerScreen.getGuiLeft() + this.getXPos();
    int y = this.containerScreen.getGuiTop() + this.getYPos();
    this.selectionWidget =
        new SelectionWidget(x + this.xOffset, y + this.yOffset, this.getXPos() + this.xOffset,
            this.getYPos() + this.yOffset, this::selectRecipe, this.containerScreen);
    this.openButton =
        new OpenSelectionButton(this.containerScreen, x, y, this.getXPos(), this.getYPos(),
            clickWidget -> this.selectionWidget.setActive(!this.selectionWidget.isActive()));
    this.openButton.visible = this.selectionWidget.getOutputWidgets().size() > 1;
  }

  @Override
  public abstract void selectRecipe(ResourceLocation recipe);

  @Override
  public SelectionWidget getSelectionWidget() {
    return selectionWidget;
  }

  @Override
  public void highlightRecipe(ResourceLocation recipe) {
    this.selectionWidget.highlightButton(recipe);
  }

  @Override
  public void setRecipes(Set<IRecipeData> recipes, ResourceLocation selected) {
    SortedSet<IRecipeData> sorted =
        new TreeSet<>(Comparator.comparing(data -> data.getOutput().getTranslationKey()));
    sorted.addAll(recipes);
    this.selectionWidget.setRecipeList(sorted);
    this.openButton.visible = recipes.size() > 1;

    if (selected != null) {
      this.highlightRecipe(selected);
    }
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.selectionWidget.render(matrixStack, mouseX, mouseY, partialTicks);
    this.openButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.openButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.selectionWidget.mouseClicked(mouseX, mouseY, button)) {
      this.selectionWidget.setActive(false);
      return true;
    } else if (this.selectionWidget.isActive()) {

      if (!this.openButton.mouseClicked(mouseX, mouseY, button)) {
        this.selectionWidget.setActive(false);
      }
      return true;
    }
    return false;
  }

  @Override
  public int getXPos() {
    return this.getOutputSlot().xPos + BUTTON_X_OFFSET;
  }

  @Override
  public int getYPos() {
    return this.getOutputSlot().yPos + BUTTON_Y_OFFSET;
  }
}
