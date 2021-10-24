package top.theillusivec4.polymorph.common.capability;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.impl.PolymorphPacketDistributor;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceContainer;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceTileEntity;

public class FurnaceRecipeProcessor
    extends AbstractRecipeProcessor<AbstractFurnaceTileEntity, IInventory, AbstractCookingRecipe> {

  protected ItemStack lastFailedInput = ItemStack.EMPTY;

  public FurnaceRecipeProcessor(AbstractFurnaceTileEntity tileEntity) {
    super(tileEntity);
  }

  @Override
  public Optional<AbstractCookingRecipe> getRecipe(World world) {
    ItemStack input = this.getTileEntity().getStackInSlot(0);

    if (input == lastFailedInput) {
      return Optional.empty();
    }
    Optional<AbstractCookingRecipe> maybeRecipe = super.getRecipe(world);

    if (!maybeRecipe.isPresent()) {
      lastFailedInput = input;
    }
    return maybeRecipe;
  }

  @Override
  public IInventory getInventory() {
    return this.getTileEntity();
  }

  @Override
  public IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {
    return ((AccessorAbstractFurnaceTileEntity) this.getTileEntity()).getRecipeType();
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe,
                                PlayerEntity selectingPlayer) {
    super.setSelectedRecipe(recipe, selectingPlayer);
    World world = this.getTileEntity().getWorld();

    if (world instanceof ServerWorld) {
      ((ServerWorld) world).getPlayers().forEach(player -> {
        if (player.openContainer instanceof AbstractFurnaceContainer &&
            ((AccessorAbstractFurnaceContainer) player.openContainer).getFurnaceInventory() ==
                this.getTileEntity()) {
          PolymorphApi.common().getPacketDistributor().sendHighlightRecipeS2C(player, recipe.getId());
        }
      });
    }
  }
}
