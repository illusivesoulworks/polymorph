package top.theillusivec4.polymorph.common.capability;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;

public class PlayerRecipeData extends AbstractRecipeData<PlayerEntity> implements
    IPlayerRecipeData {

  public PlayerRecipeData(PlayerEntity pOwner) {
    super(pOwner);
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
