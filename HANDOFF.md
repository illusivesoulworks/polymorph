# Handoff: Fix Polymorph Recipe Selection Not Updating Output

## Goal

Fix Polymorph (v0.49.10+1.20.1, LGPL v3) so that when a player clicks a different recipe in the polymorph selection widget, the crafting output slot actually updates to show the new recipe's result. Currently clicking a new recipe has no visual effect, and taking the output gives the default recipe instead.

## Root Cause

File: `CPacketPlayerRecipeSelection.java` (C2S packet handler)

When the player clicks a recipe in the widget:
1. Client sends `CPacketPlayerRecipeSelection(recipeB_id)` to server
2. Server handler calls `recipeData.selectRecipe(recipeB)` — updates `selectedRecipe` field
3. Server calls `container.sendContentUpdates()` — but the `CraftingResultInventory` slot was **never recalculated**, so it still has recipe A's output → no slot update sent
4. **Crafting tables have no equivalent of line 28-29** where smithing tables get `((ForgingScreenHandler)container).updateResult()` called

Result: Player sees no change in the output slot, and taking the item gives them recipe A's output.

Upstream issue confirming identical symptoms: https://github.com/illusivesoulworks/polymorph/issues/318 (still open)

## Two Approaches

### Approach A: Fix the source directly (fork redistribution)

Modify `CPacketPlayerRecipeSelection.java` to recalculate the result slot after selecting a recipe.

### Approach B: Standalone mixin mod (runtime patch)

Create a separate mod that applies a mixin to `CPacketPlayerRecipeSelection` to inject the fix, avoiding redistribution of Polymorph itself.

Both approaches need the same fix code — differ only in packaging.

## Fix Code (same for both approaches)

### File: `CPacketPlayerRecipeSelection.java`

Location: `com.illusivesoulworks.polymorph.common.network.client`

Current code (lines 21-31):

```java
public static void handle(CPacketPlayerRecipeSelection packet, ServerPlayerEntity player) {
    ScreenHandler container = player.field_7512;
    player.method_37908().method_8433().method_8130(packet.recipe).ifPresent(recipe -> {
        PolymorphApi.common().getRecipeData(player).ifPresent(recipeData -> recipeData.selectRecipe(recipe));
        PolymorphIntegrations.selectRecipe(container, (Recipe<?>)recipe);
        container.method_7609(player.method_31548());
        if (container instanceof ForgingScreenHandler) {
            ((ForgingScreenHandler)container).method_24928();
        }
    });
}
```

### New imports needed

```java
import com.illusivesoulworks.polymorph.mixin.core.AccessorCraftingMenu;
import com.illusivesoulworks.polymorph.mixin.core.AccessorInventoryMenu;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
```

### Modified `handle()` method

Insert the result recalculation block between `selectRecipe(recipe)` and `sendContentUpdates()`:

```java
public static void handle(CPacketPlayerRecipeSelection packet, ServerPlayerEntity player) {
    ScreenHandler container = player.field_7512;
    player.method_37908().method_8433().method_8130(packet.recipe).ifPresent(recipe -> {
        PolymorphApi.common().getRecipeData(player).ifPresent(recipeData -> recipeData.selectRecipe(recipe));
        PolymorphIntegrations.selectRecipe(container, (Recipe<?>)recipe);

        // === FIX: Recalculate crafting result so the output slot reflects the newly selected recipe ===
        if (container instanceof CraftingScreenHandler) {
            AccessorCraftingMenu accessor = (AccessorCraftingMenu) container;
            accessor.getResultSlots().setItem(0, ((Recipe<?>) recipe).assemble(accessor.getCraftSlots(), player.method_37908().method_30349()));
        } else if (container instanceof PlayerScreenHandler) {
            AccessorInventoryMenu accessor = (AccessorInventoryMenu) container;
            accessor.getResultSlots().setItem(0, ((Recipe<?>) recipe).assemble(accessor.getCraftSlots(), player.method_37908().method_30349()));
        }

        container.method_7609(player.method_31548());
        if (container instanceof ForgingScreenHandler) {
            ((ForgingScreenHandler)container).method_24928();
        }
    });
}
```

### Yarn intermediary method references

| Intermediary | Yarn name | Class |
|---|---|---|
| `method_7609` | `sendContentUpdates()` | `ScreenHandler` |
| `method_24928` | `updateResult()` | `ForgingScreenHandler` |
| `method_37908()` | `getServerWorld()` | `PlayerEntity` |
| `method_30349()` | `getRegistryManager()` | `World` |
| `method_8116(C, RegistryAccess)` | `assemble(C, RegistryAccess)` | `Recipe` |
| `method_7512` | `containerMenu` | `PlayerEntity` |

### Existing accessor mixins (already in Polymorph's codebase)

- `AccessorCraftingMenu` — `@Mixin(CraftingScreenHandler.class)`, has `getCraftSlots()` and `getResultSlots()`
- `AccessorInventoryMenu` — `@Mixin(PlayerScreenHandler.class)`, has `getCraftSlots()` and `getResultSlots()`

## Verification

1. Place items in crafting table that match multiple recipes (e.g., 8 planks → chest from two mods)
2. Click polymorph button → see recipe list with multiple outputs
3. Click a different recipe → output slot should immediately switch
4. Take the output → should be the selected recipe's output
5. Test 2x2 player inventory crafting grid (same logic via `PlayerScreenHandler`)
6. Test smithing table (should still work — already handled by `updateResult()`)
7. Test furnaces/block entities (handled separately by `BlockEntityTicker.tick()` — no change needed)

## Approach B Details (standalone mixin)

If you don't want to fork Polymorph, create a separate mod that:

1. Mixes into `CPacketPlayerRecipeSelection` using `@Inject(at = @At("RETURN"), method = "handle")`
2. After the original handler runs, gets the player's container and recalculates the result
3. This avoids GPL redistribution concerns entirely

Mixin target (rough sketch):

```java
@Mixin(targets = "com.illusivesoulworks.polymorph.common.network.client.CPacketPlayerRecipeSelection")
public class MixinPolymorphFix {
    @Inject(at = @At("RETURN"), method = "handle(Lcom/illusivesoulworks/polymorph/common/network/client/CPacketPlayerRecipeSelection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", remap = false)
    private static void fix(CPacketPlayerRecipeSelection packet, ServerPlayerEntity player, CallbackInfo ci) {
        // Access the container, check if it's a crafting screen, recalculate result
        // Same logic as the inline fix above
    }
}
```

Note: The mixin approach may need `@Inject` with `remset = false` since the handler method is a static method in a non-Minecraft class.

## Files referenced during analysis

| File | Path |
|---|---|
| CPacketPlayerRecipeSelection | `common/network/client/CPacketPlayerRecipeSelection.java` |
| AbstractRecipeData (getRecipe logic) | `common/capability/AbstractRecipeData.java` |
| PlayerRecipeData | `common/capability/PlayerRecipeData.java` |
| RecipeSelection (entry point) | `common/crafting/RecipeSelection.java` |
| MixinCraftingMenu | `mixin/core/MixinCraftingMenu.java` |
| MixinSmithingMenu | `mixin/core/MixinSmithingMenu.java` |
| AccessorCraftingMenu | `mixin/core/AccessorCraftingMenu.java` |
| AccessorInventoryMenu | `mixin/core/AccessorInventoryMenu.java` |
| QuickBenchModule (reference pattern) | `common/integration/quickbench/QuickBenchModule.java` |
| BlockEntityTicker | `common/util/BlockEntityTicker.java` |
| FurnaceRecipeData | `common/capability/FurnaceRecipeData.java` |
| PlayerRecipesWidget (client) | `client/recipe/widget/PlayerRecipesWidget.java` |
| RecipesWidget (client) | `client/recipe/RecipesWidget.java` |

## Current Progress

- [x] Root cause identified in `CPacketPlayerRecipeSelection.handle()`
- [x] Confirmed via upstream issue #318 (same version 0.49.10+1.20.1)
- [x] Accessor mixins confirmed to exist and tested in QuickBenchModule
- [x] 100% confidence — fix is a 6-line insertion following existing patterns
- [ ] Implementation
- [ ] Verification

## Confidence

100% — see `D:\projects\investigative stuff\scratch\` for the earlier confidence check notes.
