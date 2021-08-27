package top.theillusivec4.polymorph.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;

public class PolymorphCapabilities {

  @CapabilityInject(ITileEntityRecipeSelector.class)
  public static final Capability<ITileEntityRecipeSelector> TILE_ENTITY_RECIPE_SELECTOR;

  public static final ResourceLocation TILE_ENTITY_RECIPE_SELECTOR_ID =
      new ResourceLocation("polymorph:tile_entity_recipe_selector");

  public static LazyOptional<ITileEntityRecipeSelector> getRecipeSelector(TileEntity te) {
    return te.getCapability(TILE_ENTITY_RECIPE_SELECTOR);
  }

  static {
    TILE_ENTITY_RECIPE_SELECTOR = null;
  }
}
