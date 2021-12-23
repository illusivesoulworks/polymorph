package top.theillusivec4.polymorph.common.component;

import java.util.SortedSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.RecipePair;

public abstract class AbstractHighlightedRecipeData<E extends BlockEntity>
    extends AbstractBlockEntityRecipeData<E> {

  public AbstractHighlightedRecipeData(E pOwner) {
    super(pOwner);
  }

  @Override
  public void selectRecipe(Recipe<?> pRecipe) {
    super.selectRecipe(pRecipe);

    for (ServerPlayerEntity listeningPlayer : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendHighlightRecipeS2C(listeningPlayer, pRecipe.getId());
    }
  }

  @Override
  public Pair<SortedSet<RecipePair>, Identifier> getPacketData() {
    SortedSet<RecipePair> recipesList = this.getRecipesList();
    Identifier selected = null;

    if (!recipesList.isEmpty()) {
      selected =
          this.getSelectedRecipe().map(Recipe::getId).orElse(recipesList.first().getIdentifier());
    }
    return new Pair<>(recipesList, selected);
  }
}
