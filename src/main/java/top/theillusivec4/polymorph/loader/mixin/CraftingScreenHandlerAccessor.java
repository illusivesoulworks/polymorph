package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingScreenHandlerAccessor {

  @Accessor
  CraftingInventory getInput();
}
