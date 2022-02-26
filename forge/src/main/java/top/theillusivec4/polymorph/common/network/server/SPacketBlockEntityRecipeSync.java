package top.theillusivec4.polymorph.common.network.server;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.impl.RecipePair;

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

  public static void encode(SPacketBlockEntityRecipeSync pPacket, FriendlyByteBuf pBuffer) {
    pBuffer.writeBlockPos(pPacket.getBlockPos());
    pBuffer.writeResourceLocation(pPacket.getSelected());
  }

  public static SPacketBlockEntityRecipeSync decode(FriendlyByteBuf pBuffer) {
    return new SPacketBlockEntityRecipeSync(pBuffer.readBlockPos(), pBuffer.readResourceLocation());
  }

  public static void handle(SPacketBlockEntityRecipeSync pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> ClientPacketHandler.handle(pPacket)));
    pContext.get().setPacketHandled(true);
  }
}
