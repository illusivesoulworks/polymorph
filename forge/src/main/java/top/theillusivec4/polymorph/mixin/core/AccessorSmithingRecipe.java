package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingRecipe.class)
public interface AccessorSmithingRecipe {

  @Accessor
  Ingredient getBase();

  @Accessor
  Ingredient getAddition();
}
