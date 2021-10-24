package top.theillusivec4.polymorph.api.common.base;

import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public interface IPolymorphPacketDistributor {

  void sendCraftingSelectionC2S(ResourceLocation recipe);

  void sendRecipeSelectionC2S(ResourceLocation recipe);

  void sendRecipesRequestC2S();

  void sendRecipesListS2C(ServerPlayerEntity player);

  void sendRecipesListS2C(ServerPlayerEntity player, Set<IRecipeData> recipes);

  void sendRecipesListS2C(ServerPlayerEntity player, Set<IRecipeData> recipes,
                          ResourceLocation selected);

  void sendHighlightRecipeS2C(ServerPlayerEntity player, ResourceLocation recipe);

  void sendCraftingActionS2C(ServerPlayerEntity player, ResourceLocation recipe, boolean add);
}
