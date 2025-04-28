package com.avp.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import com.avp.common.armor.ArmorMaterials;

public class MK50ArmorItem extends ArmorItem {

    private static final int MK50_DURABILITY_MULTIPLIER = 14;

    public MK50ArmorItem(ArmorItem.Type type) {
        super(ArmorMaterials.MK50, type, new Item.Properties().durability(type.getDurability(MK50_DURABILITY_MULTIPLIER)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(
            Component.translatable("tooltip.avp.mk50_suit.full_set_bonus")
                .withStyle(ChatFormatting.YELLOW)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.mk50_suit.radiation_resistance")
                .withStyle(ChatFormatting.GREEN)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.mk50_suit.water_breathing")
                .withStyle(ChatFormatting.GREEN)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.mk50_suit.slowness")
                .withStyle(ChatFormatting.RED)
        );
    }
}
