package top.theillusivec4.polymorph.common.capability;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;

public class PlayerRecipeData extends AbstractRecipeData<PlayerEntity> implements
    IPlayerRecipeData {

  public PlayerRecipeData(PlayerEntity pOwner) {
    super(pOwner);
  }

  @Override
  public void syncRecipesList(Set<IRecipePair> pRecipesList) {

    if (this.getOwner() instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) this.getOwner(), pRecipesList);
    }
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
