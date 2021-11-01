package top.theillusivec4.polymorph.mixin.integration.appliedenergistics2;

import appeng.container.me.items.CraftingTermContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingTermContainer.class)
public interface AccessorCraftingTermContainer {

  @Accessor(remap = false)
  void setCurrentRecipe(IRecipe<CraftingInventory> pCurrentRecipe);
}
