package top.theillusivec4.polymorph.common.impl;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.PolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.api.common.component.StackRecipeData;
import top.theillusivec4.polymorph.common.component.AbstractStackRecipeData;
import top.theillusivec4.polymorph.common.component.PolymorphComponents;

public class PolymorphCommonImpl implements PolymorphCommon {

  private static final PolymorphCommon INSTANCE = new PolymorphCommonImpl();

  public static PolymorphCommon get() {
    return INSTANCE;
  }

  private final List<ScreenHandler2BlockEntity> screenHandler2BlockEntities = new ArrayList<>();
  private final List<ScreenHandler2ItemStack> screenHandler2ItemStacks = new ArrayList<>();
  private final PolymorphPacketDistributor distributor = new PolymorphPacketDistributorImpl();
  private final Map<Item, Item2RecipeData> itemStack2RecipeData = new HashMap<>();
  private final Map<Class<? extends BlockEntity>, BlockEntity2RecipeData> blockEntity2RecipeData =
      new HashMap<>();

  private MinecraftServer server = null;

  @Override
  public Map<Class<? extends BlockEntity>, BlockEntity2RecipeData> getAllBlockRecipeData() {
    return ImmutableMap.copyOf(this.blockEntity2RecipeData);
  }

  @Override
  public Optional<BlockEntityRecipeData> getRecipeData(BlockEntity pBlockEntity) {
    return PolymorphComponents.getRecipeData(pBlockEntity);
  }

  @Override
  public Optional<BlockEntityRecipeData> getRecipeDataFromBlockEntity(
      ScreenHandler pScreenHandler) {

    for (ScreenHandler2BlockEntity function : this.screenHandler2BlockEntities) {
      BlockEntity blockEntity = function.getBlockEntity(pScreenHandler);

      if (blockEntity != null) {
        return this.getRecipeData(blockEntity);
      }
    }
    return Optional.empty();
  }

  @Override
  public Map<Item, Item2RecipeData> getAllItemRecipeData() {
    return ImmutableMap.copyOf(this.itemStack2RecipeData);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<StackRecipeData> getRecipeData(ItemStack pStack) {
    return (Optional<StackRecipeData>) (Object) PolymorphComponents.getRecipeData(pStack);
  }

  @Override
  public Optional<StackRecipeData> getRecipeDataFromItemStack(ScreenHandler pScreenHandler) {

    for (ScreenHandler2ItemStack function : this.screenHandler2ItemStacks) {
      ItemStack itemstack = function.getItemStack(pScreenHandler);

      if (!itemstack.isEmpty()) {
        return this.getRecipeData(itemstack);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<PlayerRecipeData> getRecipeData(PlayerEntity pPlayer) {
    return PolymorphComponents.getRecipeData(pPlayer);
  }

  @Override
  public void registerBlockEntity2RecipeData(Class<? extends BlockEntity> pBlockEntity,
                                             BlockEntity2RecipeData pBlockEntity2RecipeData) {
    this.blockEntity2RecipeData.put(pBlockEntity, pBlockEntity2RecipeData);
  }

  @Override
  public void registerScreenHandler2BlockEntity(
      ScreenHandler2BlockEntity pScreenHandler2BlockEntity) {
    this.screenHandler2BlockEntities.add(0, pScreenHandler2BlockEntity);
  }

  @Override
  public void registerItem2RecipeData(Item pItem, Item2RecipeData pItemStack2RecipeData) {
    this.itemStack2RecipeData.put(pItem, pItemStack2RecipeData);
  }

  @Override
  public void registerScreenHandler2ItemStack(ScreenHandler2ItemStack pScreenHandler2ItemStack) {
    this.screenHandler2ItemStacks.add(0, pScreenHandler2ItemStack);
  }

  @Override
  public PolymorphPacketDistributor getPacketDistributor() {
    return this.distributor;
  }

  @Override
  public void setServer(MinecraftServer pServer) {
    this.server = pServer;
  }

  @Override
  public Optional<MinecraftServer> getServer() {
    return Optional.ofNullable(this.server);
  }
}
