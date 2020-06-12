package top.theillusivec4.polymorph.network.client;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.server.SPacketSyncOutput;

public class CPacketSetRecipe {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(WorkbenchContainer.class, "field_75162_e");

  private final String recipe;

  public CPacketSetRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSetRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static CPacketSetRecipe decode(PacketBuffer buf) {
    return new CPacketSetRecipe(buf.readString());
  }

  public static void handle(CPacketSetRecipe msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        AtomicReference<ItemStack> output = new AtomicReference<>(ItemStack.EMPTY);

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
                output.set(res.getCraftingResult(finalCraftingInventory));
                slot.putStack(output.get());
              }
            }
          });
        }
        Polymorph.LOGGER.info("Set done!");
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
            new SPacketSyncOutput(output.get()));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
