package com.human.common.gameplay.block_item;

import com.lib.common.data.TooltipHintBuilder;
import com.lib.common.model.TooltipCategoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.data.TooltipTranslationKeys;
import com.avp.common.registry.init.block.AVPBlocks;

public class SentryTurretBlockItem extends BlockItem {

    private static final List<Component> TOOLTIP_COMPONENTS = new TooltipHintBuilder()
        .addCategory(TooltipCategoryType.REQUIREMENTS)
        .addNegativeEffect(TooltipTranslationKeys.REQUIRES_REDSTONE_POWER)
        .addNegativeEffect(TooltipTranslationKeys.REQUIRES_NEARBY_AMMO_CHEST_WITH_AMMO)
        .build();

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
        tooltipComponents.addAll(TOOLTIP_COMPONENTS);
    }
}
