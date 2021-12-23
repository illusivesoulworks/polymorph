package top.theillusivec4.polymorph.api.common.base;

import java.util.SortedSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface PolymorphPacketDistributor {

  void sendPlayerRecipeSelectionC2S(Identifier pIdentifier);

  void sendPersistentRecipeSelectionC2S(Identifier pIdentifier);

  void sendStackRecipeSelectionC2S(Identifier pIdentifier);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList,
                          Identifier pSelected);

  void sendHighlightRecipeS2C(ServerPlayerEntity pPlayer, Identifier pIdentifier);

  void sendPlayerSyncS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList,
                         Identifier pSelected);
}
