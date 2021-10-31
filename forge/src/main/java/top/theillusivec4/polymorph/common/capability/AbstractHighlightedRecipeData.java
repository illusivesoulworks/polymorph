package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public abstract class AbstractHighlightedRecipeData<E extends TileEntity>
    extends AbstractTileEntityRecipeData<E> {

  public AbstractHighlightedRecipeData(E pOwner) {
    super(pOwner);
  }

  @Override
  public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {
    super.selectRecipe(pRecipe);

    for (ServerPlayerEntity listeningPlayer : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendHighlightRecipeS2C(listeningPlayer, pRecipe.getId());
    }
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
