package top.theillusivec4.polymorph.mixin.util.integration;

import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class JustEnoughItemsMixinHooks {

  public static void selectRecipe(Object recipe) {

    if (recipe instanceof IRecipe<?>) {
      PolymorphApi.common().getPacketDistributor()
          .sendPlayerRecipeSelectionC2S(((IRecipe<?>) recipe).getId());
    }
  }
}
