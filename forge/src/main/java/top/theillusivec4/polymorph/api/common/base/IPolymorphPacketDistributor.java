package top.theillusivec4.polymorph.api.common.base;

import java.util.Set;
import java.util.SortedSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public interface IPolymorphPacketDistributor {

  void sendPlayerRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendPersistentRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendStackRecipeSelectionC2S(ResourceLocation pResourceLocation);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<IRecipePair> pRecipesList);

  void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<IRecipePair> pRecipesList,
                          ResourceLocation pSelected);

  void sendHighlightRecipeS2C(ServerPlayerEntity pPlayer, ResourceLocation pResourceLocation);
}
