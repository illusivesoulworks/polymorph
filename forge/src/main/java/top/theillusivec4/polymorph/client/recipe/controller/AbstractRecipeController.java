package top.theillusivec4.polymorph.client.recipe.controller;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.type.IRecipeController;
import top.theillusivec4.polymorph.client.gui.RecipeSelectorGui;
import top.theillusivec4.polymorph.client.gui.ToggleSelectorButton;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketSelectCraft;

public abstract class AbstractRecipeController<I extends IInventory, R extends IRecipe<I>>
    implements IRecipeController<I, R> {

  public static final ResourceLocation TOGGLE = new ResourceLocation(PolymorphMod.MOD_ID,
      "textures/gui/toggle.png");

  public static final int SELECTOR_X_OFFSET = -4;
  public static final int SELECTOR_Y_OFFSET = -26;

  protected RecipeSelectorGui<I, R> recipeSelectorGui;
  protected Button toggleButton;
  protected final ContainerScreen<?> parentScreen;

  public AbstractRecipeController(ContainerScreen<?> screen) {
    this.parentScreen = screen;
  }

  protected void init() {
    int x = this.parentScreen.getGuiLeft() + this.getXPos();
    int y = this.parentScreen.getGuiTop() + this.getYPos();
    this.recipeSelectorGui =
        new RecipeSelectorGui<>(x + SELECTOR_X_OFFSET, y + SELECTOR_Y_OFFSET,
            this.getXPos() + SELECTOR_X_OFFSET, this.getYPos() + SELECTOR_Y_OFFSET,
            this.getInventory(), this::selectRecipe, this.parentScreen);
    this.toggleButton =
        new ToggleSelectorButton(this.parentScreen, x, y, this.getXPos(), this.getYPos(), 16, 16, 0,
            0, 17, TOGGLE,
            clickWidget -> recipeSelectorGui.setActive(!recipeSelectorGui.isActive()));
    this.toggleButton.visible = this.recipeSelectorGui.getOutputWidgets().size() > 1;
  }

  @Override
  public RecipeSelectorGui<I, R> getSelectorGui() {
    return recipeSelectorGui;
  }

  @Override
  public void selectRecipe(R recipe) {
    PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectCraft(recipe.getId()));
  }

  @Override
  public void highlightRecipe(String recipe) {
    this.recipeSelectorGui.highlightButton(recipe);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setRecipes(Set<ResourceLocation> recipes, World world, ResourceLocation selected) {
    List<R> list = new ArrayList<>();

    for (ResourceLocation recipe : recipes) {
      world.getRecipeManager().getRecipe(recipe).ifPresent(result -> list.add((R) result));
    }
    Set<RecipeOutput> recipeOutputs = new HashSet<>();
    list.removeIf(rec -> !recipeOutputs
        .add(new RecipeOutput(rec.getCraftingResult(this.getInventory()))));
    this.recipeSelectorGui.setRecipes(list);
    this.toggleButton.visible = recipes.size() > 1;

    if (selected != null) {
      this.highlightRecipe(selected.toString());
    }
  }

  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

    if (this.isActive()) {
      this.recipeSelectorGui.render(matrixStack, mouseX, mouseY, partialTicks);
      this.toggleButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.isActive()) {

      if (this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
        return true;
      } else if (this.recipeSelectorGui.mouseClicked(mouseX, mouseY, button)) {
        this.recipeSelectorGui.setActive(false);
        return true;
      } else if (this.recipeSelectorGui.isActive()) {

        if (!this.toggleButton.mouseClicked(mouseX, mouseY, button)) {
          this.recipeSelectorGui.setActive(false);
        }
        return true;
      }
    }
    return false;
  }

  static class RecipeOutput {

    private final Item item;
    private final int count;
    private final CompoundNBT tag;

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
