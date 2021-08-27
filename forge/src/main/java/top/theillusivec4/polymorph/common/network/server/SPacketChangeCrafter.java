package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class SPacketChangeCrafter {

  private final ResourceLocation recipe;
  private final boolean add;

  public SPacketChangeCrafter(ResourceLocation recipe, boolean add) {
    this.recipe = recipe;
    this.add = add;
  }

  public static void encode(SPacketChangeCrafter msg, PacketBuffer buf) {
    buf.writeResourceLocation(msg.recipe);
    buf.writeBoolean(msg.add);
  }

  public static SPacketChangeCrafter decode(PacketBuffer buf) {
    return new SPacketChangeCrafter(buf.readResourceLocation(), buf.readBoolean());
  }

  public static void handle(SPacketChangeCrafter msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

      if (clientPlayerEntity != null) {

        if (msg.add) {
          CraftingPlayers.add(clientPlayerEntity, msg.recipe);
        } else {
          CraftingPlayers.remove(clientPlayerEntity);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
