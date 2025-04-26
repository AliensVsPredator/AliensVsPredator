package com.avp.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import com.avp.common.armor.ArmorMaterials;

public class PressureSuitArmorItem extends ArmorItem {

    private static final int PRESSURE_DURABILITY_MULTIPLIER = 12;

    public PressureSuitArmorItem(Type type) {
        super(ArmorMaterials.PRESSURE, type, new Properties().durability(type.getDurability(PRESSURE_DURABILITY_MULTIPLIER)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, list, tooltipFlag);

        list.add(
            Component.translatable("tooltip.avp.pressure_suit.full_set_bonus")
                .withStyle(ChatFormatting.YELLOW)
        );
        list.add(
            Component.translatable("tooltip.avp.pressure_suit.water_breathing")
                .withStyle(ChatFormatting.GREEN)
        );
    }
}
