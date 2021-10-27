package top.theillusivec4.polymorph.api.client.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Set;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.client.widget.SelectionWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface IRecipesWidget {

  void initChildWidgets();

  void selectRecipe(ResourceLocation pResourceLocation);

  void highlightRecipe(ResourceLocation pResourceLocation);

  void setRecipesList(Set<IRecipePair> pRecipesList, ResourceLocation pSelected);

  void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pRenderPartialTicks);

  boolean mouseClicked(double pMouseX, double pMouseY, int pButton);

  Slot getOutputSlot();

  SelectionWidget getSelectionWidget();

  int getXPos();

  int getYPos();
}
