package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.polymorph.api.common.capability.IStackRecipeData;

public class StackRecipeData extends AbstractRecipeData<ItemStack> implements IStackRecipeData {

  private final Set<ServerPlayerEntity> listeners;

  public StackRecipeData(ItemStack pOwner) {
    super(pOwner);
    this.listeners = new HashSet<>();
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    return this.listeners;
  }
}
