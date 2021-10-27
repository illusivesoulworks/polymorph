package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public abstract class AbstractTileEntityRecipeData<E extends TileEntity>
    extends AbstractRecipeData<TileEntity> implements ITileEntityRecipeData {

  private boolean isFailing;

  public AbstractTileEntityRecipeData(E pOwner) {
    super(pOwner);
  }

  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    Optional<T> recipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);
    isFailing = !recipe.isPresent();
    return recipe;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E getOwner() {
    return (E) super.getOwner();
  }

  @Override
  public void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe) {
    super.setSelectedRecipe(pRecipe);

    for (ServerPlayerEntity listeningPlayer : this.getListeningPlayers()) {
      PolymorphApi.common().getPacketDistributor()
          .sendHighlightRecipeS2C(listeningPlayer, pRecipe.getId());
    }
  }

  public abstract boolean isEmpty();

  public abstract List<ServerPlayerEntity> getListeningPlayers();

  public boolean isFailing() {
    return this.isEmpty() || this.isFailing;
  }

  public void setFailing(boolean pFailing) {
    this.isFailing = pFailing;
  }

  @Override
  public boolean isEmpty(IInventory pInventory) {
    return this.isEmpty();
  }

  @Override
  public void syncRecipesList(Set<IRecipePair> pRecipesList) {
    // NO-OP
  }

  @Override
  public void syncRecipesList(ServerPlayerEntity pPlayer) {
    Set<IRecipePair> recipesList = new HashSet<>();
    ResourceLocation selected = null;

    if (!this.isFailing()) {
      recipesList.addAll(this.getRecipesList());

      if (!recipesList.isEmpty()) {
        selected = this.getSelectedRecipe().map(IRecipe::getId)
            .orElse(recipesList.stream().findFirst().get().getResourceLocation());
      }
    }
    PolymorphApi.common().getPacketDistributor().sendRecipesListS2C(pPlayer, recipesList, selected);
  }
}
