package top.theillusivec4.polymorph.mixin.integration;

import appeng.container.me.items.CraftingTermContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingTermContainer.class)
public interface AccessorAppliedEnergistics {

  @Accessor
  void setCurrentRecipe(Recipe<CraftingInventory> recipe);
}
