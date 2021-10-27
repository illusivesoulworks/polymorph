package top.theillusivec4.polymorph.common.capability;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
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
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public abstract class AbstractRecipeData<E> implements IRecipeData<E> {

  private final Set<IRecipePair> recipesList;
  private final E owner;

  private IRecipe<?> lastRecipe;
  private IRecipe<?> selectedRecipe;
  private ResourceLocation loadedRecipe;

  public AbstractRecipeData(E pOwner) {
    this.recipesList = new HashSet<>();
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
          this.setSelectedRecipe(selected);
          this.loadedRecipe = null;
        });

    if (this.isEmpty(pInventory)) {
      this.syncRecipesList(new HashSet<>());
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
      this.syncRecipesList(this.getRecipesList());
      return Optional.of(result);
    }
    SortedSet<IRecipePair> newDataset =
        new TreeSet<>(Comparator.comparing(pair -> pair.getOutput().getTranslationKey()));
    List<T> recipes =
        pRecipes.isEmpty() ? pWorld.getRecipeManager().getRecipes(pType, pInventory, pWorld) :
            pRecipes;

    if (recipes.isEmpty()) {
      this.syncRecipesList(new HashSet<>());
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
    this.setRecipeDataset(newDataset);
    this.syncRecipesList(newDataset);
    result = ref.get();
    result = result != null ? result : recipes.get(0);
    this.lastRecipe = result;
    this.setSelectedRecipe(result);
    return Optional.of(result);
  }

  public abstract void syncRecipesList(Set<IRecipePair> pRecipesList);

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
  public Set<IRecipePair> getRecipesList() {
    return this.recipesList;
  }

  @Override
  public void setRecipeDataset(@Nonnull Set<IRecipePair> pData) {
    this.recipesList.clear();
    this.recipesList.addAll(pData);
  }

  @Override
  public abstract boolean isEmpty(IInventory pInventory);

  @Override
  public E getOwner() {
    return this.owner;
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
