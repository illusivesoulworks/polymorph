package top.theillusivec4.polymorph.common.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.mixin.AbstractFurnaceContainerMixin;

public class FurnaceSelector implements IPersistentSelector {

  private final AbstractFurnaceTileEntity parent;
  private final List<AbstractCookingRecipe> recipes = new ArrayList<>();

  private ResourceLocation recipeKey;
  private AbstractCookingRecipe selectedRecipe;
  private AbstractCookingRecipe lastRecipe;

  public FurnaceSelector(AbstractFurnaceTileEntity tileEntity) {
    this.parent = tileEntity;
  }

  @Override
  public ResourceLocation getRecipeKey() {
    return null;
  }

  @Override
  public List<IRecipe<?>> getRecipes() {
    return null;
  }

  @Override
  public void setRecipes(List<? extends IRecipe<?>> recipes) {

  }

  @Nonnull
  @Override
  public Optional<IRecipe<?>> getSelectedRecipe() {

    if (selectedRecipe != null && this.parent.getWorld() != null &&
        !selectedRecipe.matches(this.parent, this.parent.getWorld())) {
      this.selectedRecipe = null;
    }
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe) {
    this.selectedRecipe = (AbstractCookingRecipe) recipe;
    World world = this.parent.getWorld();

    if (world instanceof ServerWorld) {
      ((ServerWorld) world).getPlayers().forEach(player -> {
        if (player.openContainer instanceof AbstractFurnaceContainer &&
            ((AbstractFurnaceContainerMixin) player.openContainer).getFurnaceInventory() ==
                this.parent) {
          NetworkManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
              new SPacketHighlightRecipe(recipe.getId().toString()));
        }
      });
    }
  }

  @Override
  public Optional<IRecipe<?>> getLastRecipe() {
    return Optional.ofNullable(lastRecipe);
  }

  @Override
  public void setLastRecipe(IRecipe<?> recipe) {
    this.lastRecipe = (AbstractCookingRecipe) recipe;
  }

  @Override
  public TileEntity getParent() {
    return this.parent;
  }
}
