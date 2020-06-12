package top.theillusivec4.polymorph.common.network.client;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.server.SPacketSyncOutput;

public class CPacketTransferRecipe {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(WorkbenchContainer.class, "field_75162_e");

  private final String recipe;

  public CPacketTransferRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketTransferRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static CPacketTransferRecipe decode(PacketBuffer buf) {
    return new CPacketTransferRecipe(buf.readString());
  }

  public static void handle(CPacketTransferRecipe msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        if (container instanceof WorkbenchContainer) {
          WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;
          int outputSlot = workbenchContainer.getOutputSlot();
          Slot slot = workbenchContainer.getSlot(outputSlot);
          CraftingInventory craftingInventory = null;

          try {
            craftingInventory = (CraftingInventory) CRAFT_MATRIX.get(workbenchContainer);
          } catch (IllegalAccessException e) {
            Polymorph.LOGGER.error("beep beep");
          }

          @SuppressWarnings("unchecked") Optional<ICraftingRecipe> result = (Optional<ICraftingRecipe>) sender
              .getServerWorld().getRecipeManager().getRecipe(new ResourceLocation(msg.recipe));

          CraftingInventory finalCraftingInventory = craftingInventory;
          result.ifPresent(res -> {
            if (finalCraftingInventory != null) {

              if (res.matches(finalCraftingInventory, sender.world)) {
                ItemStack itemstack = workbenchContainer.transferStackInSlot(sender, outputSlot);
                slot.putStack(res.getCraftingResult(finalCraftingInventory));

                while (!itemstack.isEmpty() && ItemStack
                    .areItemsEqual(slot.getStack(), itemstack)) {
                  itemstack = workbenchContainer.transferStackInSlot(sender, outputSlot);

                  if (res.matches(finalCraftingInventory, sender.world)) {
                    slot.putStack(res.getCraftingResult(finalCraftingInventory));
                  }
                }
              }
            }
          });
        }
        Polymorph.LOGGER.info("Transfer done!");
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
            new SPacketSyncOutput(ItemStack.EMPTY));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
