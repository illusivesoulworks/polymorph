package com.illusivesoulworks.polymorph.mixin.core;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface AccessorSmithingTrimRecipe {

  @Accessor
  Ingredient getBase();

  @Accessor
  Ingredient getAddition();

  @Accessor
  Ingredient getTemplate();
}
