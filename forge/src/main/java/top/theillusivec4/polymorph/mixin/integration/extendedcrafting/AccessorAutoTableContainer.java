package top.theillusivec4.polymorph.mixin.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.container.BasicAutoTableContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicAutoTableContainer.class)
public interface AccessorAutoTableContainer {

  @Accessor(remap = false)
  BlockPos getPos();

  @Accessor(remap = false)
  World getWorld();
}
