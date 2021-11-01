package top.theillusivec4.polymorph.mixin.integration.appliedenergistics2;

import appeng.container.me.items.PatternTermContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PatternTermContainer.class)
public interface AccessorPatternTermContainer {

  @Accessor(remap = false)
  void setCurrentRecipe(ICraftingRecipe pCurrentRecipe);

  @Invoker(remap = false)
  ItemStack callGetAndUpdateOutput();
}
