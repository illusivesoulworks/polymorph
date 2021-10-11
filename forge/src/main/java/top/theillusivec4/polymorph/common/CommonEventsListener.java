package top.theillusivec4.polymorph.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CommonEventsListener {

  @SubscribeEvent
  public void openContainer(final PlayerContainerEvent.Open evt) {
    PlayerEntity player = evt.getPlayer();

    if (!player.world.isRemote() && player instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
      Container container = evt.getContainer();

      for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {

        if (integration.openContainer(container, serverPlayerEntity)) {
          return;
        }
      }

//      for (Slot inventorySlot : container.inventorySlots) {
//
//        if (inventorySlot.inventory instanceof CraftingInventory) {
//          Set<ResourceLocation> recipes = player.world.getRecipeManager()
//              .getRecipes(IRecipeType.CRAFTING, (CraftingInventory) inventorySlot.inventory,
//                  player.world).stream().map(IRecipe::getId).collect(Collectors.toSet());
//          PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity),
//              new SPacketSendRecipes(recipes, new ResourceLocation("")));
//          return;
//        }
//      }
    }
  }

  @SubscribeEvent
  public void attachCapabilities(AttachCapabilitiesEvent<TileEntity> evt) {
    PolymorphApi.getInstance().getTileEntityRecipeSelector(evt.getObject())
        .ifPresent(selector -> evt
            .addCapability(PolymorphCapabilities.TILE_ENTITY_RECIPE_SELECTOR_ID,
                new Provider(selector)));
  }

  private static class Provider implements ICapabilitySerializable<INBT> {
    final ITileEntityRecipeSelector selector;
    final LazyOptional<ITileEntityRecipeSelector> capability;

    public Provider(ITileEntityRecipeSelector selector) {
      this.selector = selector;
      this.capability = LazyOptional.of(() -> this.selector);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                             @Nullable Direction side) {
      return PolymorphCapabilities.TILE_ENTITY_RECIPE_SELECTOR.orEmpty(cap, this.capability);
    }

    @Override
    public INBT serializeNBT() {
      return PolymorphCapabilities.TILE_ENTITY_RECIPE_SELECTOR.writeNBT(this.selector, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      PolymorphCapabilities.TILE_ENTITY_RECIPE_SELECTOR.readNBT(this.selector, null, nbt);
    }
  }
}
