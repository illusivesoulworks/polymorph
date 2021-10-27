package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;

public class TileCrafterRecipeData extends AbstractTileEntityRecipeData<TileCrafter> {

  public TileCrafterRecipeData(TileCrafter pOwner) {
    super(pOwner);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public List<ServerPlayerEntity> getListeningPlayers() {
    return new ArrayList<>();
  }
}
