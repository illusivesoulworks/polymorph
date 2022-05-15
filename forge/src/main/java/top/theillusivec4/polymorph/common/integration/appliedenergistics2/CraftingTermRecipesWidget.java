package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.menu.me.items.CraftingTermMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.mixin.integration.appliedenergistics2.AccessorCraftingTermMenu;

public class CraftingTermRecipesWidget extends PlayerRecipesWidget {

  private final CraftingTermMenu menu;

  public CraftingTermRecipesWidget(CraftingTermMenu menu, AbstractContainerScreen<?> pHandledScreen,
                                   Slot pOutputSlot) {
    super(pHandledScreen, pOutputSlot);
    this.menu = menu;
  }

  @Override
  public void selectRecipe(ResourceLocation resourceLocation) {
    super.selectRecipe(resourceLocation);
    this.menu.getPlayerInventory().player.getLevel().getRecipeManager()
        .byKey(resourceLocation).ifPresent(recipe -> {
          ((AccessorCraftingTermMenu) this.menu).setCurrentRecipe((CraftingRecipe) recipe);
          ((AccessorCraftingTermMenu) this.menu).callUpdateCurrentRecipeAndOutput(true);
        });
  }
}
