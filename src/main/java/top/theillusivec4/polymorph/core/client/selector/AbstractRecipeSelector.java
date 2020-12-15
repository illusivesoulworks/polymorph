package top.theillusivec4.polymorph.core.client.selector;

import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.PolyProvider;
import top.theillusivec4.polymorph.api.type.RecipeSelector;
import top.theillusivec4.polymorph.core.Polymorph;
import top.theillusivec4.polymorph.core.client.gui.RecipeSelectorGui;
import top.theillusivec4.polymorph.core.client.gui.ToggleRecipeButton;
import top.theillusivec4.polymorph.loader.mixin.HandledScreenAccessor;

public abstract class AbstractRecipeSelector<I extends Inventory, R extends Recipe<I>>
    implements RecipeSelector<I, R> {

  public static final Identifier TOGGLE = new Identifier(Polymorph.MODID,
      "textures/gui/toggle.png");
  private static final int SELECTOR_X_OFFSET = -4;
  private static final int SELECTOR_Y_OFFSET = -26;

  protected final RecipeSelectorGui<I, R> recipeSelectorGui;
  protected final AbstractButtonWidget toggleButton;
  protected final PolyProvider<I, R> provider;
  protected final HandledScreen<?> parent;

  private boolean updatePosition = false;

  public AbstractRecipeSelector(HandledScreen<?> screen, PolyProvider<I, R> provider) {
    this.parent = screen;
    this.provider = provider;
    int x = ((HandledScreenAccessor) screen).getX() + provider.getXPos();
    int y = ((HandledScreenAccessor) screen).getY() + provider.getYPos();
    this.recipeSelectorGui =
        new RecipeSelectorGui<>(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET,
            provider.getInventory(), this::selectRecipe, this.parent);
    this.toggleButton = new ToggleRecipeButton(x, y, 16, 16, 0, 0, 17, TOGGLE,
        clickWidget -> recipeSelectorGui.setVisible(!recipeSelectorGui.isVisible()));
    this.toggleButton.visible = this.recipeSelectorGui.getButtons().size() > 1;
  }

  @Override
  public PolyProvider<I, R> getProvider() {
    return this.provider;
  }

  public void reposition() {
    int x = ((HandledScreenAccessor) this.parent).getX() + provider.getXPos();
    int y = ((HandledScreenAccessor) this.parent).getY() + provider.getYPos();
    this.recipeSelectorGui.setPosition(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET);
    this.toggleButton.x = x;
    this.toggleButton.y = y;
  }

  public void tick() {

    if (this.updatePosition) {
      this.updatePosition = false;
      this.reposition();
    }
  }

  public void clearRecipes(World world) {
    this.setRecipes(new ArrayList<>(), world, false, "");
  }

  protected void fetchRecipes() {
    Polymorph.getLoader().getPacketVendor().fetchRecipes();
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.recipeSelectorGui.render(matrixStack, mouseX, mouseY, partialTicks);
    this.toggleButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    } else if (this.recipeSelectorGui.mouseClicked(mouseX, mouseY, button)) {
      this.recipeSelectorGui.setVisible(false);
      return true;
    } else if (this.recipeSelectorGui.isVisible()) {

      if (!this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectorGui.setVisible(false);
      }
      return true;
    }
    return false;
  }

  public void markUpdatePosition() {
    this.updatePosition = true;
  }

  static class RecipeOutput {

    private final Item item;
    private final int count;
    private final CompoundTag tag;

    public RecipeOutput(ItemStack stack) {
      this.item = stack.getItem();
      this.count = stack.getCount();
      this.tag = stack.getTag();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      RecipeOutput that = (RecipeOutput) o;
      return count == that.count && item.equals(that.item) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
      return Objects.hash(item, count, tag);
    }
  }
}
