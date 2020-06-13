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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphApi;
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
    return new CPacketTransferRecipe(buf.readString(32767));
  }

  public static void handle(CPacketTransferRecipe msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        PolymorphApi.getProvider(container).ifPresent(provider -> {
          Slot slot = provider.getOutputSlot(container);
          Optional<? extends IRecipe<?>> result = sender.getServerWorld().getRecipeManager()
              .getRecipe(new ResourceLocation(msg.recipe));
          CraftingInventory finalCraftingInventory = provider.getCraftingMatrix(container);
          result.ifPresent(res -> {

            if (res instanceof ICraftingRecipe && finalCraftingInventory != null) {
              ICraftingRecipe craftingRecipe = (ICraftingRecipe) res;

              if (craftingRecipe.matches(finalCraftingInventory, sender.world)) {
                ItemStack itemstack = container.transferStackInSlot(sender, slot.getSlotIndex());
                slot.putStack(craftingRecipe.getCraftingResult(finalCraftingInventory));

                while (!itemstack.isEmpty() && ItemStack
                    .areItemsEqual(slot.getStack(), itemstack)) {
                  itemstack = container.transferStackInSlot(sender, slot.getSlotIndex());

                  if (craftingRecipe.matches(finalCraftingInventory, sender.world)) {
                    slot.putStack(craftingRecipe.getCraftingResult(finalCraftingInventory));
                  }
                }
              }
            }
          });
        });
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
            new SPacketSyncOutput(ItemStack.EMPTY));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
