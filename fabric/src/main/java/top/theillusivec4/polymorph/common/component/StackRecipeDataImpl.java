package top.theillusivec4.polymorph.common.component;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.api.common.component.StackRecipeData;

public class StackRecipeDataImpl extends AbstractRecipeData<ItemStack> implements StackRecipeData {

  public StackRecipeDataImpl(ItemStack pOwner) {
    super(pOwner);
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    Set<ServerPlayerEntity> players = new HashSet<>();
    PolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getServer().ifPresent(server -> {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
        commonApi.getRecipeDataFromItemStack(player.currentScreenHandler)
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
