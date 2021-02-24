/*
 * Copyright (c) 2021 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.common.integration.originsclasses;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.originsclasses.power.ClassPowerTypes;
import io.github.apace100.originsclasses.power.CraftAmountPower;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

/*
 * Class methods taken from io.github.apace100.originsclasses.mixin.CraftingScreenHandlerMixin
 * from Origins: Classes by Apace100 distributed under the MIT License
 */
public class OriginsClassesIntegration {

  public static void modifyCraftingResult(PlayerEntity player, ItemStack itemStack) {

    if(itemStack.getItem().isFood() && ClassPowerTypes.BETTER_CRAFTED_FOOD.isActive(player)) {
      FoodComponent food = itemStack.getItem().getFoodComponent();
      int foodBonus = (int)Math.ceil((float)food.getHunger() / 3F);
      if(foodBonus < 1) {
        foodBonus = 1;
      }
      itemStack.getOrCreateTag().putInt("FoodBonus", foodBonus);
    }

    if(ClassPowerTypes.QUALITY_EQUIPMENT.isActive(player) && isEquipment(itemStack)) {
      addQualityAttribute(itemStack);
    }
    int baseValue = itemStack.getCount();
    int newValue = (int) OriginComponent.modify(player, CraftAmountPower.class, baseValue, (p -> p.doesApply(itemStack)));
    if(newValue != baseValue) {
      itemStack.setCount(newValue < 0 ? 0 : Math.min(newValue, itemStack.getMaxCount()));
    }
  }

  private static void addQualityAttribute(ItemStack stack) {
    Item item = stack.getItem();
    if(item instanceof ArmorItem) {
      EquipmentSlot slot = ((ArmorItem)item).getSlotType();
      stack.addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier("Blacksmith quality", 0.25D, EntityAttributeModifier.Operation.ADDITION), slot);
    } else if(item instanceof SwordItem || item instanceof RangedWeaponItem) {
      stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("Blacksmith quality", 0.5D, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);
    } else if(item instanceof MiningToolItem || item instanceof ShearsItem) {
      stack.getOrCreateTag().putFloat("MiningSpeedMultiplier", 1.05F);
    } else if(item instanceof ShieldItem) {
      stack.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier("Blacksmith quality", 0.1D, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.OFFHAND);
    }
  }

  private static boolean isEquipment(ItemStack stack) {
    Item item = stack.getItem();
    if(item instanceof ArmorItem)
      return true;
    if(item instanceof ToolItem)
      return true;
    if(item instanceof RangedWeaponItem)
      return true;
    if(item instanceof ShieldItem)
      return true;
    return false;
  }
}
