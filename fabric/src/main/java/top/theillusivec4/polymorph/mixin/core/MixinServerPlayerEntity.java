package top.theillusivec4.polymorph.mixin.core;

import com.mojang.authlib.GameProfile;
import java.util.OptionalInt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.CommonEventsListener;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

  public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
    super(world, pos, yaw, profile);
  }

  @Inject(
      at = @At("RETURN"),
      method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;")
  private void polymorph$openHandledScreen(CallbackInfoReturnable<OptionalInt> cir) {
    cir.getReturnValue().ifPresent(
        value -> CommonEventsListener.openScreenHandler((ServerPlayerEntity) (Object) this));
  }
}
