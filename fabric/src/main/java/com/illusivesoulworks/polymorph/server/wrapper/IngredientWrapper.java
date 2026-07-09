package com.illusivesoulworks.polymorph.server.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientWrapper {

  private final Ingredient ingredient;

  public IngredientWrapper(Ingredient pIngredient) {
    this.ingredient = pIngredient;
  }

  public Ingredient getIngredient() {
    return this.ingredient;
  }

  public boolean matches(IngredientWrapper pIngredient) {

    if (pIngredient == null) {
      return false;
    }
    Ingredient otherIngredient = pIngredient.getIngredient();

    if (otherIngredient == null) {
      return false;
    } else if (otherIngredient == Ingredient.EMPTY) {
      return this.ingredient == Ingredient.EMPTY;
    } else {
      ItemStack[] stacks = this.ingredient.getItems();

      for (ItemStack otherStack : pIngredient.getIngredient().getItems()) {

        for (ItemStack stack : stacks) {

          if (ItemStack.matches(stack, otherStack)) {
            return true;
          }
        }
      }
      return false;
    }
  }
}
