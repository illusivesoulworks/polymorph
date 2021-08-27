package top.theillusivec4.polymorph.api.type;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Set;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipeController<I extends IInventory, R extends IRecipe<I>> {

  void selectRecipe(R recipe);

  void highlightRecipe(String recipe);

  void setRecipes(Set<ResourceLocation> recipes, World world, ResourceLocation selected);

  default void tick() {
    // NO-OP
  }

  void render(MatrixStack matrixStack, int mouseX, int mouseY, float renderPartialTicks);

  boolean mouseClicked(double mouseX, double mouseY, int button);

  I getInventory();

  Slot getOutputSlot();

  default int getXPos() {
    return getOutputSlot().xPos;
  }

  default int getYPos() {
    return getOutputSlot().yPos - 22;
  }

  default boolean isActive() {
    return true;
  }
}
