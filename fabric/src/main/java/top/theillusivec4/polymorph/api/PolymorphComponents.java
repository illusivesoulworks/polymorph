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

package top.theillusivec4.polymorph.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.type.BlockEntityRecipeSelector;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphComponents {

  public static final ComponentKey<BlockEntityRecipeSelector> BLOCK_ENTITY_RECIPE_SELECTOR =
      ComponentRegistry
          .getOrCreate(new Identifier(PolymorphMod.MOD_ID, "block_entity_recipe_selector"),
              BlockEntityRecipeSelector.class);
}
