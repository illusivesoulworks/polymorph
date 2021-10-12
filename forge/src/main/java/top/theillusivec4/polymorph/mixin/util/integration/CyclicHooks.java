package top.theillusivec4.polymorph.mixin.util.integration;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.integration.cyclic.CyclicRecipeSelector;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.common.util.FieldAccessor;

public class CyclicHooks {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getSelectedRecipe(
      ArrayList<ItemStack> stacks, World worldIn, TileEntity te) {

    if (worldIn != null && worldIn.getServer() != null) {
      List<T> recipe = new ArrayList<>();
      PolymorphCapabilities.getRecipeSelector(te).ifPresent(recipeSelector -> {
        if (recipeSelector instanceof CyclicRecipeSelector) {
          Optional<T> maybeSelected = (Optional<T>) recipeSelector.getSelectedRecipe();
          CraftingInventory craftingInventory =
              new CraftingInventory(new FakeContainer(ContainerType.CRAFTING, 18291239), 3, 3);

          for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {
              int indexInArray = i + j * 3;
              ItemStack stack = stacks.get(indexInArray);
              craftingInventory.setInventorySlotContents(indexInArray, stack.copy());
            }
          }
          ((CyclicRecipeSelector) recipeSelector).setCraftingInventory(craftingInventory);

          maybeSelected.ifPresent(res -> {

            if (res.matches((C) craftingInventory, worldIn)) {
              recipe.add(res);
            } else {
              recipeSelector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
            }
          });

          if (!maybeSelected.isPresent()) {
            recipeSelector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
          }
        }
      });
      return recipe.isEmpty() ? Optional.empty() : Optional.of(recipe.get(0));
    } else {
      return Optional.empty();
    }
  }

  public static void sendRecipes(TileCrafter tileCrafter) {
    PolymorphCapabilities.getRecipeSelector(tileCrafter).ifPresent(recipeSelector -> {
      if (recipeSelector instanceof CyclicRecipeSelector) {
        Set<ResourceLocation> recipes = ((CyclicRecipeSelector) recipeSelector).getLastRecipes();
        World world = tileCrafter.getWorld();

        if (world instanceof ServerWorld) {

          for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {

            if (player.openContainer instanceof ContainerCrafter) {
              TileCrafter crafter = (TileCrafter) FieldAccessor.read(player.openContainer, "tile");

              if (crafter == tileCrafter) {
                PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SPacketSendRecipes(recipes, new ResourceLocation("")));
              }
            }
          }
        }
      }
    });
  }

  public static class FakeContainer extends Container {

    public FakeContainer(ContainerType<?> type, int id) {
      super(type, id);
    }

    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
      return true;
    }
  }
}
