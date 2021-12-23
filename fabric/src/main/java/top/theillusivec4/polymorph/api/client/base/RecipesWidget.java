package top.theillusivec4.polymorph.api.client.base;

import java.util.Set;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.client.widget.SelectionWidget;
import top.theillusivec4.polymorph.api.common.base.RecipePair;

public interface RecipesWidget {

  void initChildWidgets();

  void selectRecipe(Identifier pIdentifier);

  void highlightRecipe(Identifier pIdentifier);

  void setRecipesList(Set<RecipePair> pRecipesList, Identifier pSelected);

  void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pRenderPartialTicks);

  boolean mouseClicked(double pMouseX, double pMouseY, int pButton);

  Slot getOutputSlot();

  SelectionWidget getSelectionWidget();

  int getXPos();

  int getYPos();
}
