package top.theillusivec4.polymorph.common.integrations.jei;

import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class JeiMixinHooks {

  public static void setRecipe(Object recipe) {

    if (recipe instanceof IRecipe<?>) {
      RecipeSelectorManager.setPreferredRecipe(((IRecipe<?>) recipe).getId());
    }
  }
}
