package top.theillusivec4.polymorph.core.base.common;

public interface PacketVendor {

  void sendSetRecipe(String recipeId);

  void sendTransferRecipe(String recipeId);
}
