package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {

  @Accessor
  ClientRecipeBook getRecipeBook();
}
