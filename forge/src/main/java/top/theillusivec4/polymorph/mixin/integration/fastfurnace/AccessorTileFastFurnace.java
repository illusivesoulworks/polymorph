package top.theillusivec4.polymorph.mixin.integration.fastfurnace;

import net.minecraft.item.crafting.AbstractCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import shadows.fastfurnace.tile.TileFastBlastFurnace;
import shadows.fastfurnace.tile.TileFastFurnace;
import shadows.fastfurnace.tile.TileFastSmoker;

@Mixin({TileFastFurnace.class, TileFastSmoker.class, TileFastBlastFurnace.class})
public interface AccessorTileFastFurnace {

  @Accessor(remap = false)
  void setCurRecipe(AbstractCookingRecipe pRecipe);
}
