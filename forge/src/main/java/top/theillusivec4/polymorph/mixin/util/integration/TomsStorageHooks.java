package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class TomsStorageHooks {

  public static void sendRecipes(World world, CraftingInventory inv,
                                 List<IContainerListener> listeners) {

    if (!world.isRemote()) {
      Set<ResourceLocation> recipes =
          world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, inv, world).stream()
              .map(IRecipe::getId).collect(Collectors.toSet());
      SPacketSendRecipes packet = new SPacketSendRecipes(recipes, new ResourceLocation(""));

      for (IContainerListener listener : listeners) {

        if (listener instanceof ServerPlayerEntity) {
          PolymorphNetwork.INSTANCE.send(
              PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) listener), packet);
        }
      }
    }
  }
}
