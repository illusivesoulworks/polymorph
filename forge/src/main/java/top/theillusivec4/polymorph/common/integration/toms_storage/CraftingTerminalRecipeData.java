package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;

public class CraftingTerminalRecipeData
    extends AbstractTileEntityRecipeData<TileEntityCraftingTerminal> {

  public CraftingTerminalRecipeData(TileEntityCraftingTerminal pOwner) {
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
