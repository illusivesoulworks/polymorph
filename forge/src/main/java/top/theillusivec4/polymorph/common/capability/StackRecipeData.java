package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public class StackRecipeData extends AbstractRecipeData<ItemStack> implements IStackRecipeData {

  public StackRecipeData(ItemStack pOwner) {
    super(pOwner);
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    Set<ServerPlayerEntity> players = new HashSet<>();
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getServer().ifPresent(server -> {
      for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
        commonApi.getRecipeDataFromItemStack(player.openContainer)
            .ifPresent(recipeData -> {
              if (recipeData == this) {
                players.add(player);
              }
            });
      }
    });
    return players;
  }
}
