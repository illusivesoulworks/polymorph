package top.theillusivec4.polymorph.common.integration.tinkersconstruct;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.common.util.FieldAccessor;

public class TinkersConstructModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphClientApi.getInstance().addRecipeController(containerScreen -> {
      if (containerScreen.getContainer() instanceof CraftingStationContainer) {
        return new TinkersConstructRecipeController(containerScreen,
            (CraftingStationContainer) containerScreen.getContainer());
      }
      return null;
    });
  }

  @Override
  public boolean setRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer) {
      CraftingStationTileEntity te = ((CraftingStationContainer) container).getTile();

      if (te != null) {
        te.updateRecipe(null);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof CraftingStationContainer) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      CraftingStationTileEntity te = craftingStationContainer.getTile();

      if (te != null) {
        CraftingInventory inv = (CraftingInventory) FieldAccessor.read(te, "craftingInventory");

        if (inv != null) {
          World world = serverPlayerEntity.getEntityWorld();
          Set<ResourceLocation> recipes =
              world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, inv, world).stream()
                  .map(IRecipe::getId).collect(Collectors.toSet());
          PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity),
              new SPacketSendRecipes(recipes, new ResourceLocation("")));
          return true;
        }
      }
    }
    return false;
  }
}
