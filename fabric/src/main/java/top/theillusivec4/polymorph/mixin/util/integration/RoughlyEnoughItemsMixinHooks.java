package top.theillusivec4.polymorph.mixin.util.integration;

import me.shedaniel.rei.api.common.display.Display;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class RoughlyEnoughItemsMixinHooks {

  public static void selectRecipe(Display recipeDisplay) {
    recipeDisplay.getDisplayLocation().ifPresent(recipe -> {
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeIdentifier(recipe);
      ClientPlayNetworking.send(PolymorphPackets.SELECT_CRAFT, buf);
    });
  }
}
