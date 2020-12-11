package top.theillusivec4.polymorph.common.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class CPacketFetchRecipes {

  public static void encode(CPacketFetchRecipes msg, PacketBuffer buf) {
  }

  public static CPacketFetchRecipes decode(PacketBuffer buf) {
    return new CPacketFetchRecipes();
  }

  public static void handle(CPacketFetchRecipes msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        World world = sender.getServerWorld();
        List<String> recipes = PolymorphApi.getInstance()
            .getProvider(container)
            .map(provider -> provider
                .getRecipes(world, world.getRecipeManager()).stream()
                .map(recipe -> recipe.getId().toString())
                .collect(Collectors.toList())).orElse(new ArrayList<>());
        NetworkManager.INSTANCE
            .send(PacketDistributor.PLAYER.with(() -> sender), new SPacketSendRecipes(recipes));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
