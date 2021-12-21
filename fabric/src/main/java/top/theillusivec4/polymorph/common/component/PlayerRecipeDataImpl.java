package top.theillusivec4.polymorph.common.component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;

public class PlayerRecipeDataImpl extends AbstractRecipeData<PlayerEntity> implements
    PlayerRecipeData {

  public PlayerRecipeDataImpl(PlayerEntity pOwner) {
    super(pOwner);
  }

  @Override
  public <T extends Recipe<C>, C extends Inventory> Optional<T> getRecipe(RecipeType<T> pType,
                                                                          C pInventory,
                                                                          World pWorld,
                                                                          List<T> pRecipes) {
    Optional<T> maybeRecipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);
    this.syncPlayerRecipeData();
    return maybeRecipe;
  }

  @Override
  public void selectRecipe(Recipe<?> pRecipe) {
    super.selectRecipe(pRecipe);
    this.syncPlayerRecipeData();
  }

  private void syncPlayerRecipeData() {

    if (this.getOwner() instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendPlayerSyncS2C((ServerPlayerEntity) this.getOwner(), this.getRecipesList(),
              this.getSelectedRecipe().map(Recipe::getId).orElse(null));
    }
  }

  @Override
  public void sendRecipesListToListeners(boolean pEmpty) {
    Pair<SortedSet<RecipePair>, Identifier> packetData =
        pEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();
    PlayerEntity player = this.getOwner();

    if (player.world.isClient()) {
      RecipesWidgetControl.get().ifPresent(
          widget -> widget.setRecipesList(packetData.getLeft(), packetData.getRight()));
    } else if (player instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) player, packetData.getLeft(),
              packetData.getRight());
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    PlayerEntity player = this.getOwner();

    if (player instanceof ServerPlayerEntity) {
      return Collections.singleton((ServerPlayerEntity) player);
    } else {
      return new HashSet<>();
    }
  }
}
