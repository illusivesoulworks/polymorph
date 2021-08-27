/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.loading.FMLPaths;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphCommands {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    final int opPermissionLevel = 2;
    LiteralArgumentBuilder<CommandSource> command = Commands.literal("polymorph")
        .requires(player -> player.hasPermissionLevel(opPermissionLevel));

    command.then(
        Commands.literal("conflicts").executes(context -> findConflicts(context.getSource())));

    dispatcher.register(command);
  }

  private static int findConflicts(CommandSource source) {
    ServerWorld world = source.getWorld();
    List<String> lines = new ArrayList<>();
    Set<String> processed = new HashSet<>();
    Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();
    RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
    IntArrayList first = new IntArrayList();
    IntArrayList second = new IntArrayList();
    AtomicInteger conflictCount = new AtomicInteger();
    source
        .sendFeedback(new TranslationTextComponent("commands.polymorph.conflicts.starting"), true);

    recipes.forEach(recipe -> {
      String id = recipe.getId().toString();
      List<String> conflicts = new ArrayList<>();

      if (!processed.contains(id)) {
        processed.add(id);
        recipes.forEach(otherRecipe -> {
          String otherId = otherRecipe.getId().toString();

          // Algorithm based on the one from NoMoreRecipeConflict by stimmedCow & GotoLink
          // License is Public Domain
          if (!processed.contains(otherId) && !ItemStack
              .areItemStacksEqual(otherRecipe.getRecipeOutput(), recipe.getRecipeOutput())
              && recipe.getType() == IRecipeType.CRAFTING && recipe.getType() == otherRecipe
              .getType() && !recipe.isDynamic() && recipe.isDynamic() == otherRecipe.isDynamic()
              && areSameShape(recipe, otherRecipe)) {
            recipeItemHelper.clear();
            recipe.getIngredients().forEach(ingredient -> {
              for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
                recipeItemHelper.accountStack(matchingStack);
              }
            });
            second.clear();

            if (recipeItemHelper.canCraft(recipe, second)) {
              recipeItemHelper.clear();
              otherRecipe.getIngredients().forEach(ingredient -> {
                for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
                  recipeItemHelper.accountStack(matchingStack);
                }
              });
              first.clear();

              if (recipeItemHelper.canCraft(otherRecipe, first) && first.equals(second)) {
                processed.add(otherId);
                conflicts.add(otherId);
              }
            }
          }
        });
      }

      if (!conflicts.isEmpty()) {
        conflictCount.addAndGet(conflicts.size());
        lines.add("Conflicts with " + id + ":");
        lines.addAll(conflicts);
        lines.add("");
      }
    });

    if (!lines.isEmpty()) {
      try {
        Files.write(Paths.get(FMLPaths.GAMEDIR.get() + "/logs/conflicts.log"), lines,
            StandardCharsets.UTF_8);
      } catch (IOException e) {
        PolymorphMod.LOGGER.error("Whoops! Something went wrong writing down your conflicts :(");
      }
    }
    source.sendFeedback(
        new TranslationTextComponent("commands.polymorph.conflicts.success", conflictCount.get()),
        true);
    return Command.SINGLE_SUCCESS;
  }

  private static boolean areSameShape(IRecipe<?> recipe1, IRecipe<?> recipe2) {

    if (recipe1 instanceof IShapedRecipe && recipe2 instanceof IShapedRecipe) {
      IShapedRecipe<?> shapedRecipe1 = (IShapedRecipe<?>) recipe1;
      IShapedRecipe<?> shapedRecipe2 = (IShapedRecipe<?>) recipe2;
      return shapedRecipe1.getRecipeHeight() == shapedRecipe2.getRecipeHeight()
          && shapedRecipe1.getRecipeWidth() == shapedRecipe2.getRecipeWidth();
    }
    return true;
  }
}
