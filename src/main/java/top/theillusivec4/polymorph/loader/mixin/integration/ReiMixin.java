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

package top.theillusivec4.polymorph.loader.mixin.integration;

import java.util.function.Supplier;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.widgets.Button;
import me.shedaniel.rei.impl.InternalWidgets;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.common.integration.rei.ReiMixinHooks;

@Mixin(InternalWidgets.class)
public class ReiMixin {

  @Inject(at = @At(value = "HEAD"), method = "lambda$createAutoCraftingButtonWidget$0", remap = false)
  private static void _polymorph_hookTransfer(HandledScreen<?> screen,
                                              Supplier<RecipeDisplay> recipeDisplaySupplier,
                                              Button button, CallbackInfo cb) {
    ReiMixinHooks.setRecipe(recipeDisplaySupplier.get());
  }
}
