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
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

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

  public AbstractRecipesWidget(ContainerScreen<?> pContainerScreen, int pXOffset, int pYOffset) {
    this.containerScreen = pContainerScreen;
    this.xOffset = pXOffset;
    this.yOffset = pYOffset;
  }

  public AbstractRecipesWidget(ContainerScreen<?> pContainerScreen) {
    this(pContainerScreen, WIDGET_X_OFFSET, WIDGET_Y_OFFSET);
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
  public abstract void selectRecipe(ResourceLocation pResourceLocation);

  @Override
  public SelectionWidget getSelectionWidget() {
    return selectionWidget;
  }

  @Override
  public void highlightRecipe(ResourceLocation pResourceLocation) {
    this.selectionWidget.highlightButton(pResourceLocation);
  }

  @Override
  public void setRecipesList(Set<IRecipePair> pRecipesList, ResourceLocation pSelected) {
    SortedSet<IRecipePair> sorted =
        new TreeSet<>(Comparator.comparing(data -> data.getOutput().getTranslationKey()));
    sorted.addAll(pRecipesList);
    this.selectionWidget.setRecipeList(sorted);
    this.openButton.visible = pRecipesList.size() > 1;

    if (pSelected != null) {
      this.highlightRecipe(pSelected);
    }
  }

  @Override
  public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pRenderPartialTicks) {
    this.selectionWidget.render(pMatrixStack, pMouseX, pMouseY, pRenderPartialTicks);
    this.openButton.render(pMatrixStack, pMouseX, pMouseY, pRenderPartialTicks);
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (this.openButton.mouseClicked(pMouseX, pMouseY, pButton)) {
      return true;
    } else if (this.selectionWidget.mouseClicked(pMouseX, pMouseY, pButton)) {
      this.selectionWidget.setActive(false);
      return true;
    } else if (this.selectionWidget.isActive()) {

      if (!this.openButton.mouseClicked(pMouseX, pMouseY, pButton)) {
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
