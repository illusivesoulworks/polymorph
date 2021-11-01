package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public abstract class AbstractRecipeData<E> implements IRecipeData<E> {

  private final SortedSet<IRecipePair> recipesList;
  private final E owner;

  private IRecipe<?> lastRecipe;
  private IRecipe<?> selectedRecipe;
  private ResourceLocation loadedRecipe;
  private boolean isFailing;

  public AbstractRecipeData(E pOwner) {
    this.recipesList = new TreeSet<>();
    this.owner = pOwner;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    this.getLoadedRecipe().flatMap(id -> pWorld.getRecipeManager().getRecipe(id))
        .ifPresent(selected -> {
          try {
            if (selected.getType() == pType && ((T) selected).matches(pInventory, pWorld)) {
              this.setSelectedRecipe(selected);
            }
          } catch (ClassCastException e) {
            PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}",
                selected.getId(), pInventory);
          }
          this.loadedRecipe = null;
        });

    if (this.isEmpty(pInventory)) {
      this.setFailing(false);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    AtomicReference<T> ref = new AtomicReference<>(null);
    this.getLastRecipe().ifPresent(recipe -> {
      try {
        if (recipe.getType() == pType && ((T) recipe).matches(pInventory, pWorld)) {
          this.getSelectedRecipe().ifPresent(selected -> {
            try {
              if (selected.getType() == pType && ((T) selected).matches(pInventory, pWorld)) {
                ref.set((T) selected);
              }
            } catch (ClassCastException e) {
              PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}",
                  selected.getId(), pInventory);
            }
          });
        }
      } catch (ClassCastException e) {
        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe.getId(),
            pInventory);
      }
    });
    T result = ref.get();

    if (result != null) {
      this.setFailing(false);
      this.sendRecipesListToListeners(false);
      return Optional.of(result);
    }
    SortedSet<IRecipePair> newDataset = new TreeSet<>();
    List<T> recipes =
        pRecipes.isEmpty() ? pWorld.getRecipeManager().getRecipes(pType, pInventory, pWorld) :
            pRecipes;

    if (recipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }

    for (T entry : recipes) {
      ResourceLocation id = entry.getId();

      if (ref.get() == null &&
          this.getSelectedRecipe().map(recipe -> recipe.getId().equals(id)).orElse(false)) {
        ref.set(entry);
      }
      newDataset.add(new RecipePair(id, entry.getCraftingResult(pInventory)));
    }
    this.setRecipesList(newDataset);
    result = ref.get();
    result = result != null ? result : recipes.get(0);
    this.lastRecipe = result;
    this.setSelectedRecipe(result);
    this.setFailing(false);
    this.sendRecipesListToListeners(false);
    return Optional.of(result);
  }

  @Override
  public Optional<? extends IRecipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(this.selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe) {
    this.selectedRecipe = pRecipe;
  }

  public Optional<? extends IRecipe<?>> getLastRecipe() {
    return Optional.ofNullable(this.lastRecipe);
  }

  public Optional<ResourceLocation> getLoadedRecipe() {
    return Optional.ofNullable(this.loadedRecipe);
  }

  @Nonnull
  @Override
  public SortedSet<IRecipePair> getRecipesList() {
    return this.recipesList;
  }

  @Override
  public void setRecipesList(@Nonnull SortedSet<IRecipePair> pData) {
    this.recipesList.clear();
    this.recipesList.addAll(pData);
  }

  @Override
  public boolean isEmpty(IInventory pInventory) {

    if (pInventory != null) {

      for (int i = 0; i < pInventory.getSizeInventory(); i++) {

        if (!pInventory.getStackInSlot(i).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public E getOwner() {
    return this.owner;
  }

  @Override
  public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {
    this.setSelectedRecipe(pRecipe);
  }

  @Override
  public abstract Set<ServerPlayerEntity> getListeners();

  @Override
  public void sendRecipesListToListeners(boolean pEmpty) {
    Pair<SortedSet<IRecipePair>, ResourceLocation> packetData =
        pEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();

    for (ServerPlayerEntity listener : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C(listener, packetData.getFirst(), packetData.getSecond());
    }
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  @Override
  public boolean isFailing() {
    return this.isFailing;
  }

  @Override
  public void setFailing(boolean pFailing) {
    this.isFailing = pFailing;
  }

  @Override
  public void readNBT(CompoundNBT pCompound) {

    if (pCompound.contains("SelectedRecipe")) {
      this.loadedRecipe = new ResourceLocation(pCompound.getString("SelectedRecipe"));
    }

    if (pCompound.contains("RecipeDataSet")) {
      Set<IRecipePair> dataset = this.getRecipesList();
      dataset.clear();
      ListNBT list = pCompound.getList("RecipeDataSet", Constants.NBT.TAG_COMPOUND);

      for (INBT inbt : list) {
        CompoundNBT tag = (CompoundNBT) inbt;
        ResourceLocation id = ResourceLocation.tryCreate(tag.getString("Id"));
        ItemStack stack = ItemStack.read(tag.getCompound("ItemStack"));
        dataset.add(new RecipePair(id, stack));
      }
    }
  }

  @Override
  public CompoundNBT writeNBT() {
    CompoundNBT nbt = new CompoundNBT();
    this.getSelectedRecipe().ifPresent(
        selected -> nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString()));
    Set<IRecipePair> dataset = this.getRecipesList();

    if (!dataset.isEmpty()) {
      ListNBT list = new ListNBT();

      for (IRecipePair data : dataset) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("ItemStack", data.getOutput().write(new CompoundNBT()));
        tag.putString("Id", data.getResourceLocation().toString());
        list.add(tag);
      }
      nbt.put("RecipeDataSet", list);
    }
    return nbt;
  }
}
