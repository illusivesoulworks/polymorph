package top.theillusivec4.polymorph.api.common.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.common.base.RecipePair;

public interface RecipeData<E> extends Component {

  <T extends Recipe<C>, C extends Inventory> Optional<T> getRecipe(RecipeType<T> pType, C pInventory, World pWorld, List<T> pRecipes);

  void selectRecipe(Recipe<?> pRecipe);

  Optional<? extends Recipe<?>> getSelectedRecipe();

  void setSelectedRecipe(Recipe<?> pRecipe);

  SortedSet<RecipePair> getRecipesList();

  void setRecipesList(SortedSet<RecipePair> pData);

  boolean isEmpty(Inventory pInventory);

  Set<ServerPlayerEntity> getListeners();

  void sendRecipesListToListeners(boolean pEmpty);

  Pair<SortedSet<RecipePair>, Identifier> getPacketData();

  E getOwner();

  boolean isFailing();

  void setFailing(boolean pFailing);

  @Override
  default void writeToNbt(NbtCompound tag) {
    tag.put("Data", this.writeNbt());
  }

  NbtCompound writeNbt();

  @Override
  default void readFromNbt(NbtCompound tag) {
    this.readNbt(tag);
  }

  void readNbt(NbtCompound pCompound);
}
