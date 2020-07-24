package top.theillusivec4.polymorph.loader.impl;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.core.base.client.ClientAccessor;
import top.theillusivec4.polymorph.loader.mixin.HandledScreenAccessor;
import top.theillusivec4.polymorph.loader.mixin.RecipeBookWidgetAccessor;

public class ClientAccessorImpl implements ClientAccessor {

  @Override
  public Slot getFocusedSlot(HandledScreen<?> screen) {
    return ((HandledScreenAccessor) screen).getFocusedSlot();
  }

  @Override
  public ClientRecipeBook getRecipeBook(RecipeBookWidget recipeBookGui) {
    return ((RecipeBookWidgetAccessor) recipeBookGui).getRecipeBook();
  }
}
