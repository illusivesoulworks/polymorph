package top.theillusivec4.polymorph.mixin.util.integration;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectCraft;

public class JustEnoughItemsMixinHooks {

  public static void setRecipe(Object recipe) {

    if (recipe instanceof IRecipe<?>) {
      PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
          new CPacketSelectCraft(((IRecipe<?>) recipe).getId()));
    }
  }
}
