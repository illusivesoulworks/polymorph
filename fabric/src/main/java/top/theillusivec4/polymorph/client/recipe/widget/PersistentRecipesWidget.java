package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public abstract class PersistentRecipesWidget extends AbstractRecipesWidget {

  public PersistentRecipesWidget(HandledScreen<?> pHandledScreen) {
    super(pHandledScreen);
  }

  @Override
  public void selectRecipe(Identifier pIdentifier) {
    PolymorphApi.common().getPacketDistributor().sendPersistentRecipeSelectionC2S(pIdentifier);
  }
}
