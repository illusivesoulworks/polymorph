package top.theillusivec4.polymorph.client.recipe.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketGetRecipes;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectPersist;

public class FurnaceRecipeController
    extends AbstractRecipeController<IInventory, AbstractCookingRecipe> {

  private final Container container;
  private final IInventory inventory;
  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeController(ContainerScreen<?> screen) {
    super(screen);
    this.container = screen.getContainer();
    this.inventory = container.inventorySlots.get(0).inventory;
    this.init();
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {
    PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectPersist(recipe.getId()));
  }

  @Override
  public void tick() {
    ItemStack currentStack = this.getInventory().getStackInSlot(0);

    if (!ItemStack.areItemsEqual(currentStack, lastStack)) {
      this.lastStack = currentStack;
      World world = Minecraft.getInstance().world;

      if (world != null) {
        PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CPacketGetRecipes());
      }
    }
  }

  @Override
  public IInventory getInventory() {
    return this.inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.container.inventorySlots.get(2);
  }
}
