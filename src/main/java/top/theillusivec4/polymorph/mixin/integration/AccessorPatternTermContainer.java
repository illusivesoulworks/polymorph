package top.theillusivec4.polymorph.mixin.integration;

import appeng.container.me.items.PatternTermContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PatternTermContainer.class)
public interface AccessorPatternTermContainer {

  @Accessor
  void setCurrentRecipe(CraftingRecipe recipe);

  @Invoker
  ItemStack callGetAndUpdateOutput();
}
