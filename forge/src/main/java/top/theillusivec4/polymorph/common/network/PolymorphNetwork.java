package top.theillusivec4.polymorph.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.client.CPacketGetRecipes;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectCraft;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectPersist;
import top.theillusivec4.polymorph.common.network.server.SPacketChangeCrafter;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class PolymorphNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void register() {
    INSTANCE =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(PolymorphMod.MOD_ID, "main"))
            .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
            .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Client-to-Server
    register(CPacketSelectCraft.class, CPacketSelectCraft::encode, CPacketSelectCraft::decode,
        CPacketSelectCraft::handle);
    register(CPacketSelectPersist.class, CPacketSelectPersist::encode, CPacketSelectPersist::decode,
        CPacketSelectPersist::handle);
    register(CPacketGetRecipes.class, CPacketGetRecipes::encode, CPacketGetRecipes::decode,
        CPacketGetRecipes::handle);

    // Server-to-Client
    register(SPacketChangeCrafter.class, SPacketChangeCrafter::encode, SPacketChangeCrafter::decode,
        SPacketChangeCrafter::handle);
    register(SPacketSendRecipes.class, SPacketSendRecipes::encode, SPacketSendRecipes::decode,
        SPacketSendRecipes::handle);
    register(SPacketHighlightRecipe.class, SPacketHighlightRecipe::encode,
        SPacketHighlightRecipe::decode, SPacketHighlightRecipe::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
                                   Function<PacketBuffer, M> decoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
