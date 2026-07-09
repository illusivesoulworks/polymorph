package com.illusivesoulworks.polymorph.server.wrapper;

import com.illusivesoulworks.polymorph.mixin.core.AccessorSmithingTransformRecipe;
import com.illusivesoulworks.polymorph.mixin.core.AccessorSmithingTrimRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class SmithingRecipeWrapper extends RecipeWrapper {

  public SmithingRecipeWrapper(Recipe<?> pRecipe) {
    super(pRecipe);
  }

  @Override
  public boolean conflicts(RecipeWrapper pOther) {
    Ingredient template = Ingredient.EMPTY;
    Ingredient base = Ingredient.EMPTY;
    Ingredient addition = Ingredient.EMPTY;
    Ingredient otherTemplate = Ingredient.EMPTY;
    Ingredient otherBase = Ingredient.EMPTY;
    Ingredient otherAddition = Ingredient.EMPTY;
    Recipe<?> recipe = this.getRecipe();
    Recipe<?> otherRecipe = pOther.getRecipe();

    if (recipe instanceof SmithingTrimRecipe) {
      AccessorSmithingTrimRecipe accessorSmithingRecipe = (AccessorSmithingTrimRecipe) recipe;
      template = accessorSmithingRecipe.getTemplate();
      base = accessorSmithingRecipe.getBase();
      addition = accessorSmithingRecipe.getAddition();
    } else if (recipe instanceof SmithingTransformRecipe) {
      AccessorSmithingTransformRecipe accessorSmithingRecipe =
          (AccessorSmithingTransformRecipe) recipe;
      template = accessorSmithingRecipe.getTemplate();
      base = accessorSmithingRecipe.getBase();
      addition = accessorSmithingRecipe.getAddition();
    }

    if (otherRecipe instanceof SmithingTrimRecipe) {
      AccessorSmithingTrimRecipe accessorSmithingRecipe =
          (AccessorSmithingTrimRecipe) otherRecipe;
      otherTemplate = accessorSmithingRecipe.getTemplate();
      otherBase = accessorSmithingRecipe.getBase();
      otherAddition = accessorSmithingRecipe.getAddition();
    } else if (otherRecipe instanceof SmithingTransformRecipe) {
      AccessorSmithingTransformRecipe accessorSmithingRecipe =
          (AccessorSmithingTransformRecipe) otherRecipe;
      otherTemplate = accessorSmithingRecipe.getTemplate();
      otherBase = accessorSmithingRecipe.getBase();
      otherAddition = accessorSmithingRecipe.getAddition();
    }
    IngredientWrapper baseWrapper = new IngredientWrapper(base);
    IngredientWrapper otherBaseWrapper = new IngredientWrapper(otherBase);
    IngredientWrapper additionWrapper = new IngredientWrapper(addition);
    IngredientWrapper otherAdditionWrapper = new IngredientWrapper(otherAddition);
    IngredientWrapper templateWrapper = new IngredientWrapper(template);
    IngredientWrapper otherTemplateWrapper = new IngredientWrapper(otherTemplate);
    return super.conflicts(pOther) &&
        baseWrapper.matches(otherBaseWrapper) & additionWrapper.matches(otherAdditionWrapper) &&
        templateWrapper.matches(otherTemplateWrapper);
  }
}
