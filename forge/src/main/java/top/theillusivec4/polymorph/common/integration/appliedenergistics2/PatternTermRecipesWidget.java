package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.PatternTermContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorPatternTermContainer;

public class PatternTermRecipesWidget extends PlayerRecipesWidget {

  private final PatternTermContainer container;

  public PatternTermRecipesWidget(ContainerScreen<?> containerScreen,
                                  PatternTermContainer container, Slot outputSlot) {
    super(containerScreen, outputSlot);
    this.container = container;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    super.selectRecipe(pResourceLocation);
    this.container.getPlayerInventory().player.getEntityWorld().getRecipeManager()
        .getRecipe(pResourceLocation).ifPresent(recipe -> {
          ((AccessorPatternTermContainer) this.container).setCurrentRecipe((ICraftingRecipe) recipe);
          ((AccessorPatternTermContainer) this.container).callGetAndUpdateOutput();
        });
  }
}
