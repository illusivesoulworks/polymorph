package top.theillusivec4.polymorph.mixin.util.integration;

import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

public class RefinedStorageHooks {

  public static boolean loaded = false;

  public static <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(BlockPos pos,
                                                                                   RecipeManager recipeManager,
                                                                                   IRecipeType<T> type,
                                                                                   C inventory,
                                                                                   World world) {
    if (!world.isRemote() && loaded) {
      TileEntity te = world.getTileEntity(pos);

      if (te instanceof GridTile) {
        return MixinHooks.getSelectedRecipe(type, inventory, world, te);
      }
    }
    return recipeManager.getRecipes(type, inventory, world).stream().findFirst();
  }

  public static void sendRecipes(World world, IGrid grid, List<IContainerListener> listeners) {

    if (!world.isRemote() && grid instanceof GridNetworkNode) {
      GridNetworkNode node = (GridNetworkNode) grid;
      CraftingInventory inv = node.getCraftingMatrix();

      if (inv != null) {
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

  public static void appendPattern(boolean exactPattern, ItemStack stack, BlockPos pos,
                                   World world) {

    if (exactPattern) {
      CompoundNBT tag = stack.getTag();

      if (tag == null) {
        stack.setTag(new CompoundNBT());
      }

      if (!world.isRemote() && loaded) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof GridTile) {
          Optional<ICraftingRecipe> recipe = MixinHooks.getSelectedRecipe(IRecipeType.CRAFTING,
              ((GridTile) te).getNode().getCraftingMatrix(), world, te);
          recipe.ifPresent(
              rec -> stack.getTag().putString("PolymorphRecipe", rec.getId().toString()));
        }
      }
    }
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPatternRecipe(
      ItemStack stack, IRecipeType<T> type, C inventory, World world) {
    CompoundNBT tag = stack.getTag();

    if (tag != null) {
      String id = tag.getString("PolymorphRecipe");
      Optional<T> opt = (Optional<T>) world.getRecipeManager().getRecipe(new ResourceLocation(id));

      if (opt.isPresent()) {
        return opt;
      }
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getWirelessRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      ICraftingGridListener listener) {

    if (listener instanceof GridContainer) {
      GridContainer container = (GridContainer) listener;
      return MixinHooks.getRecipe(recipeManager, type, inventory, world, container.getPlayer());
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }
}
