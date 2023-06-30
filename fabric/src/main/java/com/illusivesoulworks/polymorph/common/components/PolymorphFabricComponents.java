/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common.components;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PolymorphFabricComponents implements BlockComponentInitializer,
    EntityComponentInitializer, ItemComponentInitializer {

  public static final ComponentKey<PlayerRecipeDataComponent> PLAYER_RECIPE_DATA =
      ComponentRegistry.getOrCreate(new ResourceLocation(PolymorphApi.MOD_ID, "player_recipe_data"),
          PlayerRecipeDataComponent.class);
  public static final ComponentKey<AbstractBlockEntityRecipeDataComponent>
      BLOCK_ENTITY_RECIPE_DATA =
      ComponentRegistry.getOrCreate(
          new ResourceLocation(PolymorphApi.MOD_ID, "block_entity_recipe_data"),
          AbstractBlockEntityRecipeDataComponent.class);
  public static final ComponentKey<AbstractStackRecipeDataComponent> STACK_RECIPE_DATA =
      ComponentRegistry.getOrCreate(new ResourceLocation(PolymorphApi.MOD_ID, "stack_recipe_data"),
          AbstractStackRecipeDataComponent.class);

  private static final Map<Item, Function<ItemStack, AbstractStackRecipeDataComponent>>
      ITEM_2_RECIPE_DATA =
      new HashMap<>();
  private static final Map<Class<? extends BlockEntity>, Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>>>
      BLOCK_ENTITY_2_RECIPE_DATA = new HashMap<>();

  public static void setup() {

  }

  public static void registerBlockEntity(Class<? extends BlockEntity> blockEntityClass,
                                         Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>> blockEntity2RecipeData) {
    BLOCK_ENTITY_2_RECIPE_DATA.put(blockEntityClass, blockEntity2RecipeData);
  }

  public static void registerItem(Item item,
                                  Function<ItemStack, AbstractStackRecipeDataComponent> stack2RecipeData) {
    ITEM_2_RECIPE_DATA.put(item, stack2RecipeData);
  }

  @Override
  public void registerBlockComponentFactories(@Nonnull BlockComponentFactoryRegistry registry) {
    registerBlockEntity(AbstractFurnaceBlockEntity.class,
        blockEntity -> new FurnaceRecipeDataComponent((AbstractFurnaceBlockEntity) blockEntity));

    for (Map.Entry<Class<? extends BlockEntity>, Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>>> entry : BLOCK_ENTITY_2_RECIPE_DATA.entrySet()) {
      registry.registerFor(entry.getKey(), BLOCK_ENTITY_RECIPE_DATA,
          blockEntity -> entry.getValue().apply(blockEntity));
    }
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(Player.class, PLAYER_RECIPE_DATA, PlayerRecipeDataComponent::new);
  }

  @Override
  public void registerItemComponentFactories(@Nonnull ItemComponentFactoryRegistry registry) {

    for (Map.Entry<Item, Function<ItemStack, AbstractStackRecipeDataComponent>> entry : ITEM_2_RECIPE_DATA.entrySet()) {
      registry.register(entry.getKey(), STACK_RECIPE_DATA, stack -> entry.getValue().apply(stack));
    }
  }
}
