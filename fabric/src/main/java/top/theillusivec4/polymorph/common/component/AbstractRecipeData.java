package top.theillusivec4.polymorph.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.api.common.component.RecipeData;
import top.theillusivec4.polymorph.api.common.event.PolymorphRecipeEvents;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipePairImpl;

public abstract class AbstractRecipeData<E> implements RecipeData<E> {

  private final SortedSet<RecipePair> recipesList;
  private final E owner;

  private Recipe<?> lastRecipe;
  private Recipe<?> selectedRecipe;
  private Identifier loadedRecipe;
  private boolean isFailing;

  public AbstractRecipeData(E pOwner) {
    this.recipesList = new TreeSet<>();
    this.owner = pOwner;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Recipe<C>, C extends Inventory> Optional<T> getRecipe(RecipeType<T> pType,
                                                                          C pInventory,
                                                                          World pWorld,
                                                                          List<T> pRecipes) {
    this.getLoadedRecipe().flatMap(id -> pWorld.getRecipeManager().get(id))
        .ifPresent(selected -> {
          try {
            if (selected.getType() == pType &&
                (((T) selected).matches(pInventory, pWorld) || isEmpty(pInventory))) {
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
    SortedSet<RecipePair> newDataset = new TreeSet<>();
    List<T> recipes =
        pRecipes.isEmpty() ? pWorld.getRecipeManager().getAllMatches(pType, pInventory, pWorld) :
            pRecipes;
    PolymorphRecipeEvents.FETCH_RECIPES.invoker().modify(this, (List<Recipe<?>>) recipes);

    if (recipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    List<T> validRecipes = new ArrayList<>();

    for (T entry : recipes) {
      Identifier id = entry.getId();
      ItemStack crafted = entry.craft(pInventory);

      if (crafted.isEmpty()) {
        continue;
      }

      if (ref.get() == null &&
          this.getSelectedRecipe().map(recipe -> recipe.getId().equals(id)).orElse(false)) {
        ref.set(entry);
      }
      newDataset.add(new RecipePairImpl(id, crafted));
      validRecipes.add(entry);
    }

    if (validRecipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    this.setRecipesList(newDataset);
    result = ref.get();
    result = result != null ? result : validRecipes.get(0);
    this.lastRecipe = result;
    this.setSelectedRecipe(result);
    this.setFailing(false);
    this.sendRecipesListToListeners(false);
    return Optional.of(result);
  }

  @Override
  public Optional<? extends Recipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(this.selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(Recipe<?> pRecipe) {
    this.selectedRecipe = pRecipe;
  }

  public Optional<? extends Recipe<?>> getLastRecipe() {
    return Optional.ofNullable(this.lastRecipe);
  }

  public Optional<Identifier> getLoadedRecipe() {
    return Optional.ofNullable(this.loadedRecipe);
  }

  @Override
  public SortedSet<RecipePair> getRecipesList() {
    return this.recipesList;
  }

  @Override
  public void setRecipesList(SortedSet<RecipePair> pData) {
    this.recipesList.clear();
    this.recipesList.addAll(pData);
  }

  @Override
  public boolean isEmpty(Inventory pInventory) {

    if (pInventory != null) {

      for (int i = 0; i < pInventory.size(); i++) {

        if (!pInventory.getStack(i).isEmpty()) {
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
  public void selectRecipe(Recipe<?> pRecipe) {
    this.setSelectedRecipe(pRecipe);
  }

  @Override
  public abstract Set<ServerPlayerEntity> getListeners();

  @Override
  public void sendRecipesListToListeners(boolean pEmpty) {
    Pair<SortedSet<RecipePair>, Identifier> packetData =
        pEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();

    for (ServerPlayerEntity listener : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C(listener, packetData.getLeft(), packetData.getRight());
    }
  }

  @Override
  public Pair<SortedSet<RecipePair>, Identifier> getPacketData() {
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
  public void readNbt(NbtCompound pCompound) {

    if (pCompound.contains("SelectedRecipe")) {
      this.loadedRecipe = new Identifier(pCompound.getString("SelectedRecipe"));
    }

    if (pCompound.contains("RecipeDataSet")) {
      Set<RecipePair> dataset = this.getRecipesList();
      dataset.clear();
      NbtList list = pCompound.getList("RecipeDataSet", NbtType.COMPOUND);

      for (NbtElement inbt : list) {
        NbtCompound tag = (NbtCompound) inbt;
        Identifier id = Identifier.tryParse(tag.getString("Id"));
        ItemStack stack = ItemStack.fromNbt(tag.getCompound("ItemStack"));
        dataset.add(new RecipePairImpl(id, stack));
      }
    }
  }

  @Override
  public NbtCompound writeNbt() {
    NbtCompound nbt = new NbtCompound();
    this.getSelectedRecipe().ifPresent(
        selected -> nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString()));
    Set<RecipePair> dataset = this.getRecipesList();

    if (!dataset.isEmpty()) {
      NbtList list = new NbtList();

      for (RecipePair data : dataset) {
        NbtCompound tag = new NbtCompound();
        tag.put("ItemStack", data.getOutput().writeNbt(new NbtCompound()));
        tag.putString("Id", data.getIdentifier().toString());
        list.add(tag);
      }
      nbt.put("RecipeDataSet", list);
    }
    return nbt;
  }
}
