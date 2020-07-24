package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerScreenHandlerAccessor {

  @Accessor
  CraftingInventory getCraftingInput();
}
