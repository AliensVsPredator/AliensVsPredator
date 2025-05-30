package com.human.common.gameplay.block_item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.registry.init.AVPBlocks;

public class SentryTurretBlockItem extends BlockItem {

    public SentryTurretBlockItem() {
        super(AVPBlocks.SENTRY_TURRET.get(), new Item.Properties());
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
