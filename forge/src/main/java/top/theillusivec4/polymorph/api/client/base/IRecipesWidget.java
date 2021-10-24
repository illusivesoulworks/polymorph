package top.theillusivec4.polymorph.api.client.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Set;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.client.widget.SelectionWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

public interface IRecipesWidget {

  void initChildWidgets();

  void selectRecipe(ResourceLocation recipe);

  void highlightRecipe(ResourceLocation recipe);

  void setRecipes(Set<IRecipeData> recipes, ResourceLocation selected);

  void render(MatrixStack matrixStack, int mouseX, int mouseY, float renderPartialTicks);

  boolean mouseClicked(double mouseX, double mouseY, int button);

  Slot getOutputSlot();

  SelectionWidget getSelectionWidget();

  int getXPos();

  int getYPos();
}
