package top.theillusivec4.polymorph.common.component;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;

public abstract class AbstractBlockEntityRecipeData<E extends BlockEntity>
    extends AbstractRecipeData<BlockEntity> implements BlockEntityRecipeData {

  private DefaultedList<ItemStack> lastInput;

  public AbstractBlockEntityRecipeData(E pOwner) {
    super(pOwner);
    this.lastInput = DefaultedList.of();
  }

  protected abstract DefaultedList<ItemStack> getInput();

  @Override
  public void tick() {
    boolean changed = false;
    DefaultedList<ItemStack> currentInput = this.getInput();
    this.lastInput = validateList(this.lastInput, currentInput.size());

    for (int i = 0; i < currentInput.size(); i++) {
      ItemStack lastStack = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);

      if (!ItemStack.areEqual(lastStack, currentStack)) {
        changed = true;
      }
      this.lastInput.set(i, currentStack.copy());
    }

    if (changed) {
      this.sendRecipesListToListeners(this.isFailing() || this.isEmpty());
    }
  }

  private DefaultedList<ItemStack> validateList(DefaultedList<ItemStack> pList, int pSize) {

    if (pList.size() == pSize) {
      return pList;
    } else {
      DefaultedList<ItemStack> resized = DefaultedList.ofSize(pSize, ItemStack.EMPTY);

      for (int i = 0; i < Math.min(resized.size(), pList.size()); i++) {
        resized.set(i, pList.get(i));
      }
      return resized;
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    World world = this.getOwner().getWorld();
    Set<ServerPlayerEntity> players = new HashSet<>();

    if (world instanceof ServerWorld) {

      for (ServerPlayerEntity player : ((ServerWorld) world).toServerWorld().getPlayers()) {
        PolymorphApi.common().getRecipeDataFromBlockEntity(player.currentScreenHandler)
            .ifPresent(recipeData -> {
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
  public Pair<SortedSet<RecipePair>, Identifier> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  @Override
  public boolean isEmpty(Inventory pInventory) {
    return this.isEmpty();
  }
}
