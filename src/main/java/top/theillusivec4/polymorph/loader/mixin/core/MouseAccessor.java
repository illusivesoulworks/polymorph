package top.theillusivec4.polymorph.loader.mixin.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mouse.class)
public interface MouseAccessor {

    @Accessor
    MinecraftClient getClient();

    @Accessor
    double getX();

    @Accessor
    double getY();
}
