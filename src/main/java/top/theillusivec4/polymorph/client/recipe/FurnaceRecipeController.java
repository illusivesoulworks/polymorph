package top.theillusivec4.polymorph.client.recipe;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class FurnaceRecipeController
    extends AbstractRecipeController<Inventory, AbstractCookingRecipe> {

  private final ScreenHandler screenHandler;
  private final Inventory inventory;
  private ItemStack lastStack = ItemStack.EMPTY;

  public FurnaceRecipeController(HandledScreen<?> screen) {
    super(screen);
    this.screenHandler = screen.getScreenHandler();
    this.inventory = screenHandler.slots.get(0).inventory;
    this.init();
  }

  @Override
  public void selectRecipe(AbstractCookingRecipe recipe) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(recipe.getId());
    ClientPlayNetworking.send(PolymorphPackets.SELECT_PERSIST, buf);
  }

  @Override
  public void tick() {
    ItemStack currentStack = this.getInventory().getStack(0);

    if (!ItemStack.areItemsEqual(currentStack, lastStack)) {
      this.lastStack = currentStack;
      World world = MinecraftClient.getInstance().world;

      if (world != null) {
        ClientPlayNetworking.send(PolymorphPackets.GET_RECIPES, PacketByteBufs.empty());
      }
    }
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.screenHandler.slots.get(2);
  }
}
