package top.theillusivec4.polymorph.common.network.client;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class CPacketGetRecipes {

  public static void encode(CPacketGetRecipes msg, PacketBuffer buf) {
    // NO-OP
  }

  public static CPacketGetRecipes decode(PacketBuffer buf) {
    return new CPacketGetRecipes();
  }

  public static void handle(CPacketGetRecipes msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        World world = sender.getEntityWorld();
        Container container = sender.openContainer;
        IInventory inventory = container.inventorySlots.get(0).inventory;

        if (inventory instanceof TileEntity) {
          PolymorphCapabilities.getRecipeSelector((TileEntity) inventory)
              .ifPresent(recipeSelector -> {
                @SuppressWarnings("unchecked") List<? extends IRecipe<?>> recipes =
                    world.getRecipeManager().getRecipes().stream()
                        .filter((val) -> val.getType() == recipeSelector.getRecipeType())
                        .flatMap((val) -> Util.streamOptional(recipeSelector.getRecipeType()
                            .matches((IRecipe<IInventory>) val, world,
                                (IInventory) recipeSelector.getParent())))
                        .sorted(Comparator.comparing(
                            (recipe) -> recipe.getRecipeOutput().getTranslationKey()))
                        .collect(Collectors.toList());
                Set<ResourceLocation> recipeIds = new HashSet<>();
                ResourceLocation selectedRecipe = new ResourceLocation("");

                if (!recipes.isEmpty()) {
                  selectedRecipe = recipeSelector.getSelectedRecipe().map(IRecipe::getId)
                      .orElse(recipes.get(0).getId());

                  for (IRecipe<?> recipe : recipes) {
                    recipeIds.add(recipe.getId());
                  }
                }
                PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
                    new SPacketSendRecipes(recipeIds, selectedRecipe));
              });
          return;
        }

        for (Slot inventorySlot : container.inventorySlots) {

          if (inventorySlot.inventory instanceof CraftingInventory) {
            Set<ResourceLocation> recipes = sender.world.getRecipeManager()
                .getRecipes(IRecipeType.CRAFTING, (CraftingInventory) inventorySlot.inventory,
                    sender.world).stream().map(IRecipe::getId).collect(Collectors.toSet());
            PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
                new SPacketSendRecipes(recipes, new ResourceLocation("")));
            return;
          }
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
