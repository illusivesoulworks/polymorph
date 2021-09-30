package top.theillusivec4.polymorph.common.integration.tomsstorage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.client.recipe.controller.CraftingRecipeController;
import top.theillusivec4.polymorph.common.integration.tomsstorage.network.CPacketSelectToms;
import top.theillusivec4.polymorph.common.integration.tomsstorage.network.TomsStoragePolymorphNetwork;

public class TomsStorageRecipeController extends CraftingRecipeController {

  public TomsStorageRecipeController(ContainerScreen<?> containerScreen,
                                     CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
  }

  @Override
  public void selectRecipe(ICraftingRecipe recipe) {
    TomsStoragePolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectToms(recipe.getId()));
  }
}
