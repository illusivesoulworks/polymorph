package com.illusivesoulworks.polymorph.mixin.core;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface AccessorSmithingTransformRecipe {

  @Accessor
  Ingredient getBase();

  @Accessor
  Ingredient getAddition();

  @Accessor
  Ingredient getTemplate();
}
