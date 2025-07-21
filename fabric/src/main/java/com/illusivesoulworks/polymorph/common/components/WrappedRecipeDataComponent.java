package com.illusivesoulworks.polymorph.common.components;

import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WrappedRecipeDataComponent<M extends BlockEntity>
    extends AbstractBlockEntityRecipeDataComponent<M> {

  public final IBlockEntityRecipeData recipeData;

  public WrappedRecipeDataComponent(IBlockEntityRecipeData recipeData) {
    super((M) recipeData.getOwner());
    this.recipeData = recipeData;
  }

  @Override
  public void readFromNbt(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registryLookup) {
    this.recipeData.readNBT(registryLookup, tag.getCompound("Data"));
  }

  @Override
  public void writeToNbt(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registryLookup) {
    tag.put("Data", this.recipeData.writeNBT(registryLookup));
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return NonNullList.create();
  }

  @Override
  public void tick() {
    this.recipeData.tick();
  }

  @Override
  public boolean isEmpty() {
    return this.recipeData.isEmpty();
  }
}
