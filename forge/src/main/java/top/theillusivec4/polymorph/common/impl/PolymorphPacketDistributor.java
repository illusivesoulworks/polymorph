package top.theillusivec4.polymorph.common.impl;

import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipesList;

public class PolymorphPacketDistributor implements IPolymorphPacketDistributor {

  @Override
  public void sendPlayerRecipeSelectionC2S(ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketPlayerRecipeSelection(pResourceLocation));
  }

  @Override
  public void sendRecipeSelectionC2S(ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketPersistentRecipeSelection(pResourceLocation));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer) {
    sendRecipesListS2C(pPlayer, null);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, Set<IRecipePair> pRecipesList) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketRecipesList(pRecipesList, null));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, Set<IRecipePair> pRecipesList,
                                 ResourceLocation pSelected) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketRecipesList(pRecipesList, pSelected));
  }

  @Override
  public void sendHighlightRecipeS2C(ServerPlayerEntity pPlayer, ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketHighlightRecipe(pResourceLocation));
  }
}
