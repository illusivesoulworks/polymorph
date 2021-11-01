package top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks;

import com.mojang.datafixers.util.Pair;
import java.util.SortedSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.capability.StackRecipeData;

public class SmeltingUpgradeStackRecipeData extends StackRecipeData {

  public SmeltingUpgradeStackRecipeData(ItemStack pOwner) {
    super(pOwner);
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    SortedSet<IRecipePair> recipesList = this.getRecipesList();
    ResourceLocation selected = null;

    if (!recipesList.isEmpty()) {
      selected = this.getSelectedRecipe().map(IRecipe::getId)
          .orElse(recipesList.first().getResourceLocation());
    }
    return new Pair<>(recipesList, selected);
  }
}
