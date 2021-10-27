package top.theillusivec4.polymorph.common.integration.tconstruct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;

public class CraftingStationRecipeData
    extends AbstractTileEntityRecipeData<CraftingStationTileEntity> {

  public CraftingStationRecipeData(CraftingStationTileEntity pOwner) {
    super(pOwner);
  }

  @Override
  public boolean isEmpty() {
    return this.getOwner().isEmpty();
  }

  @Override
  public List<ServerPlayerEntity> getListeningPlayers() {
    return new ArrayList<>();
  }

  @Override
  public void syncRecipesList(ServerPlayerEntity pPlayer) {
    Set<IRecipePair> recipesList = new HashSet<>();

    if (!this.isFailing()) {
      recipesList.addAll(this.getRecipesList());
    }
    PolymorphApi.common().getPacketDistributor().sendRecipesListS2C(pPlayer, recipesList);
  }
}
