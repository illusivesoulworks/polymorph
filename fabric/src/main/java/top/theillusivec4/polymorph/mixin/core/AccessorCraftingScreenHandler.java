package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface AccessorCraftingScreenHandler {

  @Accessor
  CraftingInventory getInput();

  @Accessor
  CraftingResultInventory getResult();

  @Accessor
  PlayerEntity getPlayer();
}
