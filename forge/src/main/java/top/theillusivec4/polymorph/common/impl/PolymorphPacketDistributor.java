package top.theillusivec4.polymorph.common.impl;

import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketCraftingSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketRecipesRequest;
import top.theillusivec4.polymorph.common.network.server.SPacketCraftingAction;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipes;

public class PolymorphPacketDistributor implements IPolymorphPacketDistributor {

  @Override
  public void sendCraftingSelectionC2S(ResourceLocation recipe) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketCraftingSelection(recipe));
  }

  @Override
  public void sendRecipeSelectionC2S(ResourceLocation recipe) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketRecipeSelection(recipe));
  }

  @Override
  public void sendRecipesRequestC2S() {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(), new CPacketRecipesRequest());
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity player) {
    sendRecipesListS2C(player, null);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity player, Set<IRecipeData> recipes) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketRecipes(recipes, null));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity player, Set<IRecipeData> recipes,
                                 ResourceLocation selected) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketRecipes(recipes, selected));
  }

  @Override
  public void sendHighlightRecipeS2C(ServerPlayerEntity player, ResourceLocation recipe) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> player),
        new SPacketHighlightRecipe(recipe));
  }

  @Override
  public void sendCraftingActionS2C(ServerPlayerEntity player, ResourceLocation recipe,
                                    boolean add) {
    PolymorphNetwork.get().send(PacketDistributor.ALL.noArg(),
        new SPacketCraftingAction(player.getEntityId(), recipe, add));
  }
}
