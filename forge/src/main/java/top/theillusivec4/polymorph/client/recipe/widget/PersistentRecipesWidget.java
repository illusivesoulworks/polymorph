package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public abstract class PersistentRecipesWidget extends AbstractRecipesWidget {

  public PersistentRecipesWidget(ContainerScreen<?> pContainerScreen) {
    super(pContainerScreen);
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendPersistentRecipeSelectionC2S(pResourceLocation);
  }
}
