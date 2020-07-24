package top.theillusivec4.polymorph.core.base.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.screen.slot.Slot;

public interface ClientAccessor {

  Slot getFocusedSlot(HandledScreen<?> screen);

  ClientRecipeBook getRecipeBook(RecipeBookWidget recipeBookGui);
}
