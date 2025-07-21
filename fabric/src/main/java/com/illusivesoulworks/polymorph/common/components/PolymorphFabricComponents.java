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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class PolymorphFabricComponents implements BlockComponentInitializer,
    EntityComponentInitializer {

  public static final ComponentKey<PlayerRecipeDataComponent> PLAYER_RECIPE_DATA =
      ComponentRegistry.getOrCreate(
          ResourceLocation.fromNamespaceAndPath(PolymorphApi.MOD_ID, "player_recipe_data"),
          PlayerRecipeDataComponent.class);
  public static final ComponentKey<AbstractBlockEntityRecipeDataComponent>
      BLOCK_ENTITY_RECIPE_DATA = ComponentRegistry.getOrCreate(
      ResourceLocation.fromNamespaceAndPath(PolymorphApi.MOD_ID, "block_entity_recipe_data"),
      AbstractBlockEntityRecipeDataComponent.class);

  private static final Map<Class<? extends BlockEntity>, Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>>>
      BLOCK_ENTITY_2_RECIPE_DATA = new HashMap<>();

  public static void setup() {

  }

  public static void registerBlockEntity(Class<? extends BlockEntity> blockEntityClass,
                                         Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>> blockEntity2RecipeData) {
    BLOCK_ENTITY_2_RECIPE_DATA.put(blockEntityClass, blockEntity2RecipeData);
  }

  @Override
  public void registerBlockComponentFactories(@Nonnull BlockComponentFactoryRegistry registry) {
    registerBlockEntity(AbstractFurnaceBlockEntity.class,
                        blockEntity -> new FurnaceRecipeDataComponent(
                            (AbstractFurnaceBlockEntity) blockEntity));

    for (Map.Entry<Class<? extends BlockEntity>, Function<BlockEntity, AbstractBlockEntityRecipeDataComponent<?>>> entry : BLOCK_ENTITY_2_RECIPE_DATA.entrySet()) {
      registry.registerFor(entry.getKey(), BLOCK_ENTITY_RECIPE_DATA,
                           blockEntity -> entry.getValue().apply(blockEntity));
    }

    for (Map.Entry<Class<? extends BlockEntity>, PolymorphApi.IRecipeDataFactory> entry : PolymorphApi.getInstance()
        .getBlockEntities().entrySet()) {
      registry.registerFor(entry.getKey(), BLOCK_ENTITY_RECIPE_DATA,
                           blockEntity -> new WrappedRecipeDataComponent<>(
                               entry.getValue().createRecipeData(blockEntity)));
    }
  }

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(Player.class, PLAYER_RECIPE_DATA, PlayerRecipeDataComponent::new);
  }
}
