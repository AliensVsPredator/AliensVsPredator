package com.avp.common.block_item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import com.avp.common.block.AVPBlocks;

public class SentryTurretBlockItem extends BlockItem {

    public SentryTurretBlockItem() {
        super(AVPBlocks.SENTRY_TURRET, new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(
            Component.translatable("tooltip.avp.sentry_turret.requires")
                .withStyle(ChatFormatting.YELLOW)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.sentry_turret.redstone_power_requirement")
                .withStyle(ChatFormatting.RED)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.sentry_turret.nearby_ammo_chest_with_ammo_requirement")
                .withStyle(ChatFormatting.RED)
        );
    }
}
