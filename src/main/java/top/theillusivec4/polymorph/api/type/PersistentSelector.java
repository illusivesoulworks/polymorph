package top.theillusivec4.polymorph.api.type;

import dev.onyxstudios.cca.api.v3.component.Component;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public interface PersistentSelector extends Component {

  Optional<Recipe<?>> fetchRecipe(World world);

  RecipeType<? extends Recipe<?>> getRecipeType();

  Optional<? extends Recipe<?>> getSelectedRecipe();

  void setSavedRecipe(String recipe);

  void setSelectedRecipe(Recipe<?> recipe);

  BlockEntity getParent();
}
