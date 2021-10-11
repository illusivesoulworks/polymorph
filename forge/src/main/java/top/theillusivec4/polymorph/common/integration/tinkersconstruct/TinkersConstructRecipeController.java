package top.theillusivec4.polymorph.common.integration.tinkersconstruct;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.client.recipe.controller.AbstractRecipeController;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectCraft;
import top.theillusivec4.polymorph.common.util.FieldAccessor;

public class TinkersConstructRecipeController
    extends AbstractRecipeController<CraftingInventory, IRecipe<CraftingInventory>> {

  private final CraftingInventory craftingInventory;
  private final CraftingStationContainer container;

  public TinkersConstructRecipeController(ContainerScreen<?> screen,
                                          CraftingStationContainer container) {
    super(screen);
    this.container = container;
    CraftingStationTileEntity te = container.getTile();
    this.craftingInventory =
        te != null ? (CraftingInventory) FieldAccessor.read(te, "craftingInventory") : null;
    this.init();
  }

  @Override
  public void selectRecipe(IRecipe<CraftingInventory> recipe) {
    PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectCraft(recipe.getId()));
  }

  @Override
  public CraftingInventory getInventory() {
    return this.craftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    return container.inventorySlots.get(9);
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos + 8;
  }
}
