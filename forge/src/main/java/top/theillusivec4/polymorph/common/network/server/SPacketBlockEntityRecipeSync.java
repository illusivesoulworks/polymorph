package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPacketBlockEntityRecipeSync {

  private final BlockPos blockPos;
  private final ResourceLocation selected;

  public SPacketBlockEntityRecipeSync(BlockPos pBlockPos, ResourceLocation pSelected) {
    this.blockPos = pBlockPos;
    this.selected = pSelected;
  }

  public BlockPos getBlockPos() {
    return this.blockPos;
  }

  public ResourceLocation getSelected() {
    return this.selected;
  }

  public static void encode(SPacketBlockEntityRecipeSync pPacket, PacketBuffer pBuffer) {
    pBuffer.writeBlockPos(pPacket.getBlockPos());
    pBuffer.writeResourceLocation(pPacket.getSelected());
  }

  public static SPacketBlockEntityRecipeSync decode(PacketBuffer pBuffer) {
    return new SPacketBlockEntityRecipeSync(pBuffer.readBlockPos(), pBuffer.readResourceLocation());
  }

  public static void handle(SPacketBlockEntityRecipeSync pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> ClientPacketHandler.handle(pPacket)));
    pContext.get().setPacketHandled(true);
  }
}
