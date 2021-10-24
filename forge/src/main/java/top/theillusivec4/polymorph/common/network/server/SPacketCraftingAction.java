package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.common.crafting.CraftingPlayers;

public class SPacketCraftingAction {

  private final int entityId;
  private final ResourceLocation recipe;
  private final boolean add;

  public SPacketCraftingAction(int entityId, ResourceLocation recipe, boolean add) {
    this.entityId = entityId;
    this.recipe = recipe;
    this.add = add;
  }

  public static void encode(SPacketCraftingAction msg, PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeResourceLocation(msg.recipe);
    buf.writeBoolean(msg.add);
  }

  public static SPacketCraftingAction decode(PacketBuffer buf) {
    return new SPacketCraftingAction(buf.readInt(), buf.readResourceLocation(), buf.readBoolean());
  }

  public static void handle(SPacketCraftingAction msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      World world = Minecraft.getInstance().world;

      if (world != null) {
        Entity entity = world.getEntityByID(msg.entityId);

        if (entity instanceof PlayerEntity) {

          if (msg.add) {
            CraftingPlayers.add((PlayerEntity) entity, msg.recipe);
          } else {
            CraftingPlayers.remove((PlayerEntity) entity);
          }
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
