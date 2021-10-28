package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;

public class PlayerRecipeData extends AbstractRecipeData<PlayerEntity> implements
    IPlayerRecipeData {

  public PlayerRecipeData(PlayerEntity pOwner) {
    super(pOwner);
  }

  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    Optional<T> maybeRecipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);

    if (this.getOwner() instanceof ServerPlayerEntity) {
      Set<IRecipePair> recipesList = new HashSet<>();

      if (maybeRecipe.isPresent()) {
        recipesList.addAll(this.getRecipesList());
      }
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) this.getOwner(), recipesList);
    }
    return maybeRecipe;
  }

  @Override
  public boolean isEmpty(IInventory pInventory) {

    for (int i = 0; i < pInventory.getSizeInventory(); i++) {

      if (!pInventory.getStackInSlot(i).isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
