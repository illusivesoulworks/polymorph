//package top.theillusivec4.polymorph.common.capability;
//
//import com.mojang.datafixers.util.Pair;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;
//import javax.annotation.Nullable;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.World;
//import top.theillusivec4.polymorph.api.common.base.IRecipePair;
//import top.theillusivec4.polymorph.common.PolymorphMod;
//import top.theillusivec4.polymorph.common.impl.RecipePair;
//import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
//
//public abstract class AbstractRecipeProcessor<T extends TileEntity, I extends IInventory, R extends IRecipe<I>>
//    extends AbstractRecipeData implements IPersistentRecipeDataset {
//
//  private final T blockEntity;
//
//  protected R selectedRecipe;
//  protected String savedRecipe = "";
//  protected NonNullList<ItemStack> lastFailedInput;
//
//  public AbstractRecipeProcessor(T tileEntity) {
//    this.blockEntity = tileEntity;
//  }
//
//  @SuppressWarnings("unchecked")
//  @Override
//  public Optional<R> getRecipe(World world) {
//    I inventory = this.getInventory();
//    NonNullList<ItemStack> input = this.getInput();
//
//    if (this.lastFailedInput == null) {
//      this.lastFailedInput = NonNullList.withSize(input.size(), ItemStack.EMPTY);
//    }
//    boolean changed = false;
//
//    for (int i = 0; i < input.size(); i++) {
//      ItemStack stack = input.get(i);
//
//      if (!ItemStack.areItemStacksEqual(stack, this.lastFailedInput.get(i))) {
//        changed = true;
//      }
//    }
//
//    if (!changed) {
//      return Optional.empty();
//    }
//
//    if (!savedRecipe.isEmpty()) {
//      List<R> recipe = new ArrayList<>();
//      try {
//        world.getRecipeManager().getRecipe(new ResourceLocation(savedRecipe)).ifPresent(result -> {
//          if (((R) result).matches(inventory, world)) {
//            this.setSelectedRecipe(result, null);
//            recipe.add((R) result);
//          }
//        });
//      } catch (ClassCastException e) {
//        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", savedRecipe,
//            this.getInventory());
//      }
//      savedRecipe = "";
//
//      if (!recipe.isEmpty()) {
//        Set<IRecipePair> dataset = this.getRecipeDataset();
//        Set<IRecipePair> valid = new HashSet<>();
//
//        for (IRecipePair data : dataset) {
//          ResourceLocation id = data.getResourceLocation();
//          try {
//            world.getRecipeManager().getRecipe(id).ifPresent(result -> {
//              if (((R) result).matches(inventory, world)) {
//                valid.add(data);
//              }
//            });
//          } catch (ClassCastException e) {
//            PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", id,
//                this.getInventory());
//          }
//        }
//        this.setRecipeDataset(valid);
//        return Optional.of(recipe.get(0));
//      }
//    }
//    SortedSet<Pair<R, ItemStack>> recipes =
//        new TreeSet<>(Comparator.comparing(pair -> pair.getSecond().getTranslationKey()));
//    this.getRecipeDataset().clear();
//
//    for (IRecipe<?> recipe : world.getRecipeManager().getRecipes()) {
//
//      try {
//        if (recipe.getType() == this.getRecipeType() &&
//            ((IRecipe<IInventory>) recipe).matches(inventory, world)) {
//          R cast = (R) recipe;
//          ItemStack output = cast.getCraftingResult(inventory);
//          recipes.add(new Pair<>(cast, output));
//          this.getRecipeDataset().add(new RecipePair(recipe.getId(), output));
//        }
//      } catch (ClassCastException e) {
//        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe,
//            this.getInventory());
//      }
//    }
//
//    if (recipes.isEmpty()) {
//      this.lastFailedInput = NonNullList.withSize(input.size(), ItemStack.EMPTY);
//
//      for (int i = 0; i < this.lastFailedInput.size(); i++) {
//        this.lastFailedInput.set(i, input.get(i));
//      }
//      return Optional.empty();
//    } else {
//      R recipe = recipes.first().getFirst();
//      this.setSelectedRecipe(recipe, null);
//      return Optional.of(recipe);
//    }
//  }
//
//  @Override
//  public boolean isInputEmpty() {
//    NonNullList<ItemStack> input = this.getInput();
//
//    for (ItemStack stack : input) {
//
//      if (!stack.isEmpty()) {
//        return false;
//      }
//    }
//    return true;
//  }
//
//  public abstract NonNullList<ItemStack> getInput();
//
//  public abstract I getInventory();
//
//  public abstract IRecipeType<? extends R> getRecipeType();
//
//  @Override
//  public Optional<R> getSelectedRecipe() {
//    return Optional.ofNullable(selectedRecipe);
//  }
//
//  @SuppressWarnings("unchecked")
//  @Override
//  public void setSelectedRecipe(IRecipe<?> recipe, @Nullable PlayerEntity selectingPlayer) {
//    try {
//      this.selectedRecipe = (R) recipe;
//    } catch (ClassCastException e) {
//      PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe,
//          this.getInventory());
//      return;
//    }
//    Container container = selectingPlayer != null ? selectingPlayer.openContainer : null;
//
//    for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {
//
//      if (integration.selectRecipe(this.blockEntity, this.selectedRecipe) ||
//          (container != null && integration.selectRecipe(container, this.selectedRecipe))) {
//        return;
//      }
//    }
//  }
//
//  @Override
//  public T getTileEntity() {
//    return this.blockEntity;
//  }
//
//
//}
