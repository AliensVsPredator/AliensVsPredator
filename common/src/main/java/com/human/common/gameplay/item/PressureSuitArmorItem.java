package com.human.common.gameplay.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.registry.init.AVPArmorMaterials;

public class PressureSuitArmorItem extends ArmorItem {

    private static final int PRESSURE_DURABILITY_MULTIPLIER = 12;

    public PressureSuitArmorItem(Type type) {
        super(
            AVPArmorMaterials.PRESSURE.getHolder(),
            type,
            new Properties().durability(type.getDurability(PRESSURE_DURABILITY_MULTIPLIER))
        );
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @NotNull TooltipContext context,
        @NotNull List<Component> tooltipComponents,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(
            Component.translatable("tooltip.avp.pressure_suit.full_set_bonus")
                .withStyle(ChatFormatting.YELLOW)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.pressure_suit.water_breathing")
                .withStyle(ChatFormatting.GREEN)
        );
    }
}
