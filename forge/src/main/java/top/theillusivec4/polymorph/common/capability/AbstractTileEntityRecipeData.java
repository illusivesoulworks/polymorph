package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public abstract class AbstractTileEntityRecipeData<E extends TileEntity>
    extends AbstractRecipeData<TileEntity> implements ITileEntityRecipeData {

  private boolean isFailing;
  private NonNullList<ItemStack> lastInput;

  public AbstractTileEntityRecipeData(E pOwner) {
    super(pOwner);
    this.lastInput = NonNullList.create();
  }

  protected abstract NonNullList<ItemStack> getInput();

  @Override
  public void tick() {
    boolean changed = false;
    NonNullList<ItemStack> currentInput = this.getInput();
    this.lastInput = validateList(this.lastInput, currentInput.size());

    for (int i = 0; i < currentInput.size(); i++) {
      ItemStack lastStack = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);

      if (!ItemStack.areItemStacksEqual(lastStack, currentStack)) {
        changed = true;
      }
      this.lastInput.set(i, currentStack.copy());
    }

    if (changed) {
      IPolymorphPacketDistributor packetDistributor = PolymorphApi.common().getPacketDistributor();
      boolean failing = this.isFailing();

      for (ServerPlayerEntity listeningPlayer : this.getListeningPlayers()) {

        if (failing) {
          packetDistributor.sendRecipesListS2C(listeningPlayer);
        } else {
          Pair<SortedSet<IRecipePair>, ResourceLocation> data = getPacketData();
          packetDistributor.sendRecipesListS2C(listeningPlayer, data.getFirst(), data.getSecond());
        }
      }
    }
  }

  private NonNullList<ItemStack> validateList(NonNullList<ItemStack> pList, int pSize) {

    if (pList.size() == pSize) {
      return pList;
    } else {
      NonNullList<ItemStack> resized = NonNullList.withSize(pSize, ItemStack.EMPTY);

      for (int i = 0; i < Math.min(resized.size(), pList.size()); i++) {
        resized.set(i, pList.get(i));
      }
      return resized;
    }
  }

  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    Optional<T> recipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);
    isFailing = !recipe.isPresent();
    return recipe;
  }

  public Set<ServerPlayerEntity> getListeningPlayers() {
    World world = this.getOwner().getWorld();
    Set<ServerPlayerEntity> players = new HashSet<>();

    if (world instanceof ServerWorld) {

      for (ServerPlayerEntity player : ((ServerWorld) world).getWorldServer().getPlayers()) {
        PolymorphApi.common().getRecipeData(player.openContainer).ifPresent(recipeData -> {
          if (recipeData == this) {
            players.add(player);
          }
        });
      }
    }
    return players;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E getOwner() {
    return (E) super.getOwner();
  }

  public boolean isEmpty() {

    for (ItemStack stack : this.getInput()) {

      if (!stack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  public boolean isFailing() {
    return this.isEmpty() || this.isFailing;
  }

  public void setFailing(boolean pFailing) {
    this.isFailing = pFailing;
  }

  @Override
  public boolean isEmpty(IInventory pInventory) {
    return this.isEmpty();
  }
}
