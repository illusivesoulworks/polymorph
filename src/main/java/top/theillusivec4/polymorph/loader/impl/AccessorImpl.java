package top.theillusivec4.polymorph.loader.impl;

import java.lang.reflect.Proxy;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import top.theillusivec4.polymorph.core.base.common.Accessor;
import top.theillusivec4.polymorph.loader.mixin.CraftingScreenHandlerAccessor;
import top.theillusivec4.polymorph.loader.mixin.IngredientAccessor;
import top.theillusivec4.polymorph.loader.mixin.PlayerScreenHandlerAccessor;

public class AccessorImpl implements Accessor {

  public CraftingInventory getCraftingInput(PlayerScreenHandler screenHandler) {
    return ((PlayerScreenHandlerAccessor) screenHandler).getCraftingInput();
  }

  public CraftingInventory getCraftingInput(CraftingScreenHandler screenHandler) {
    return ((CraftingScreenHandlerAccessor) screenHandler).getInput();
  }

  @Override
  public ItemStack[] getMatchingStacks(Ingredient ingredient) {
    IngredientAccessor accessor = (IngredientAccessor) Proxy
        .newProxyInstance(IngredientAccessor.class.getClassLoader(),
            new Class[]{IngredientAccessor.class}, (proxy, method, args) -> Ingredient.class
                .getMethod(method.getName(), method.getParameterTypes()).invoke(ingredient, args));
    return accessor.getMatchingStacks();
  }
}
