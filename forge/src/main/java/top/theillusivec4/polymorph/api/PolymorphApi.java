package top.theillusivec4.polymorph.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;

public final class PolymorphApi {

  private static final List<Function<TileEntity, ITileEntityRecipeSelector>> SELECTORS =
      new ArrayList<>();
  private static final PolymorphApi INSTANCE = new PolymorphApi();

  public static PolymorphApi getInstance() {
    return INSTANCE;
  }

  public Optional<ITileEntityRecipeSelector> getTileEntityRecipeSelector(TileEntity te) {

    for (Function<TileEntity, ITileEntityRecipeSelector> controllerFunction : SELECTORS) {
      ITileEntityRecipeSelector controller = controllerFunction.apply(te);

      if (controller != null) {
        return Optional.of(controller);
      }
    }
    return Optional.empty();
  }

  public void addTileEntity(Function<TileEntity, ITileEntityRecipeSelector> selectorFunction) {
    SELECTORS.add(selectorFunction);
  }
}
