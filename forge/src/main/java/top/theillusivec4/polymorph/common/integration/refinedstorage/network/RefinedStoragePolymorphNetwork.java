package top.theillusivec4.polymorph.common.integration.refinedstorage.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.tomsstorage.network.CPacketSelectToms;

public class RefinedStoragePolymorphNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void register() {
    INSTANCE =
        NetworkRegistry.ChannelBuilder.named(
                new ResourceLocation(PolymorphMod.MOD_ID, "refined_storage"))
            .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
            .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Client-to-Server
    register(CPacketSelectRefined.class, CPacketSelectRefined::encode, CPacketSelectRefined::decode,
        CPacketSelectRefined::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
                                   Function<PacketBuffer, M> decoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
