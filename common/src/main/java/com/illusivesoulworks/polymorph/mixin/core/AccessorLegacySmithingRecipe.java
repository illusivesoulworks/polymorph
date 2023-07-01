package com.illusivesoulworks.polymorph.mixin.core;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LegacyUpgradeRecipe.class)
public interface AccessorLegacySmithingRecipe {

  @Accessor
  Ingredient getBase();

  @Accessor
  Ingredient getAddition();
}
