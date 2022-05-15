package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.CraftingTermMenu;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermContainer;

public class CraftingTermRecipesWidget extends PlayerRecipesWidget {

  private final CraftingTermMenu menu;

  public CraftingTermRecipesWidget(CraftingTermMenu menu, HandledScreen<?> pHandledScreen,
                                   Slot pOutputSlot) {
    super(pHandledScreen, pOutputSlot);
    this.menu = menu;
  }

  @Override
  public void selectRecipe(Identifier pIdentifier) {
    super.selectRecipe(pIdentifier);
    this.menu.getPlayerInventory().player.getEntityWorld().getRecipeManager()
        .get(pIdentifier).ifPresent(recipe -> {
          ((AccessorCraftingTermContainer) this.menu).setCurrentRecipe((CraftingRecipe) recipe);
          ((AccessorCraftingTermContainer) this.menu).callUpdateCurrentRecipeAndOutput(true);
        });
  }
}
