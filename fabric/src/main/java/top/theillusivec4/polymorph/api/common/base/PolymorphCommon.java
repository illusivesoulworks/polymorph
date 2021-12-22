package top.theillusivec4.polymorph.api.common.base;

import java.util.Map;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.api.common.component.StackRecipeData;

public interface PolymorphCommon {

  Map<Class<? extends BlockEntity>, BlockEntity2RecipeData> getAllBlockRecipeData();

  Optional<BlockEntityRecipeData> getRecipeData(BlockEntity pBlockEntity);

  Optional<BlockEntityRecipeData> getRecipeDataFromBlockEntity(ScreenHandler pScreenHandler);

  Map<Item, Item2RecipeData> getAllItemRecipeData();

  Optional<StackRecipeData> getRecipeData(ItemStack pStack);

  Optional<StackRecipeData> getRecipeDataFromItemStack(ScreenHandler pScreenHandler);

  Optional<PlayerRecipeData> getRecipeData(PlayerEntity pPlayer);

  void registerBlockEntity2RecipeData(Class<? extends BlockEntity> pBlockEntity,
                                      BlockEntity2RecipeData pBlockEntity2RecipeData);

  void registerScreenHandler2BlockEntity(ScreenHandler2BlockEntity pScreenHandler2BlockEntity);

  void registerItem2RecipeData(Item pItem, Item2RecipeData pItem2RecipeData);

  void registerScreenHandler2ItemStack(ScreenHandler2ItemStack pScreenHandler2ItemStack);

  PolymorphPacketDistributor getPacketDistributor();

  void setServer(MinecraftServer pServer);

  Optional<MinecraftServer> getServer();

  interface Item2RecipeData {

    StackRecipeData createRecipeData(Item pItem);
  }

  interface BlockEntity2RecipeData {

    BlockEntityRecipeData createRecipeData(BlockEntity pBlockEntity);
  }

  interface ScreenHandler2BlockEntity {

    BlockEntity getBlockEntity(ScreenHandler pScreenHandler);
  }

  interface ScreenHandler2ItemStack {

    ItemStack getItemStack(ScreenHandler pScreenHandler);
  }
}
