package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import com.lothrazar.cyclic.item.crafting.CraftingBagContainer;
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
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.cyclic.network.CyclicPolymorphNetwork;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class CyclicModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addTileEntity(tileEntity -> {
      if (tileEntity instanceof TileCrafter) {
        return new CyclicRecipeSelector((TileCrafter) tileEntity);
      }
      return null;
    });
    CyclicPolymorphNetwork.register();
  }

  @Override
  public void clientSetup() {
    PolymorphClientApi.getInstance().addRecipeController(containerScreen -> {
      if (containerScreen instanceof ScreenCrafter &&
          containerScreen.getContainer() instanceof ContainerCrafter) {
        return new CyclicRecipeController(containerScreen,
            (ContainerCrafter) containerScreen.getContainer());
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof CraftingBagContainer) {
      CraftingInventory inv = ((CraftingBagContainer) container).getCraftMatrix();

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
    return false;
  }
}
