package top.theillusivec4.polymorph.api.type;

import java.util.List;
import java.util.Set;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.world.World;

public interface RecipeSelector<I extends Inventory, R extends Recipe<I>> {

  PolyProvider<I, R> getProvider();

  void selectRecipe(R recipe);

  void highlightRecipe(String recipe);

  void setRecipes(List<R> recipes, World world, boolean refresh, String selected);

  void setRecipes(Set<String> recipes, World world, boolean refresh, String selected);

  void tick();

  void render(MatrixStack matrixStack, int mouseX, int mouseY, float renderPartialTicks);

  boolean mouseClicked(double mouseX, double mouseY, int button);

  void markUpdatePosition();
}
