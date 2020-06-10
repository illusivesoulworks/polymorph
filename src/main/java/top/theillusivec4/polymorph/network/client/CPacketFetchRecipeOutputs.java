package top.theillusivec4.polymorph.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.server.SPacketSendRecipeOutputs;

public class CPacketFetchRecipeOutputs {

  public CPacketFetchRecipeOutputs() {

  }

  public static void encode(CPacketFetchRecipeOutputs msg, PacketBuffer buf) {

  }

  public static CPacketFetchRecipeOutputs decode(PacketBuffer buf) {
    return new CPacketFetchRecipeOutputs();
  }

  public static void handle(CPacketFetchRecipeOutputs msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        if (container instanceof WorkbenchContainer) {
          WorkbenchContainer workbenchContainer = (WorkbenchContainer) container;
          CraftingInventory craftingInventory = null;
          CraftResultInventory resultInventory = null;

          for (Slot slot : workbenchContainer.inventorySlots) {

            if (slot.inventory instanceof CraftingInventory) {
              craftingInventory = (CraftingInventory) slot.inventory;
            }

            if (slot.inventory instanceof CraftResultInventory) {
              resultInventory = (CraftResultInventory) slot.inventory;
            }

            if (craftingInventory != null && resultInventory != null) {
              break;
            }
          }

          if (craftingInventory != null && resultInventory != null) {
            List<ICraftingRecipe> recipes = sender.getServerWorld().getRecipeManager()
                .getRecipes(IRecipeType.CRAFTING, craftingInventory, sender.getEntityWorld());
            List<ItemStack> outputs = new ArrayList<>();
            recipes.forEach(recipe -> outputs.add(recipe.getRecipeOutput()));
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
                new SPacketSendRecipeOutputs(outputs));
          }
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
