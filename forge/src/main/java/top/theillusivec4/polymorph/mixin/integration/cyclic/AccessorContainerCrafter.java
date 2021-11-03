package top.theillusivec4.polymorph.mixin.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerCrafter.class)
public interface AccessorContainerCrafter {

  @Accessor(remap = false)
  TileCrafter getTile();
}
