package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingInventory.class)
public interface AccessorCraftingInventory {

  @Accessor
  ScreenHandler getHandler();
}
