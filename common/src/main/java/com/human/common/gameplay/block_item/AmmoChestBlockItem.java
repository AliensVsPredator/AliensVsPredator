package com.human.common.gameplay.block_item;

import com.lib.common.data.TooltipHintBuilder;
import com.lib.common.model.TooltipCategoryType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.data.TooltipTranslationKeys;
import com.avp.common.registry.init.block.AVPBlocks;

public class AmmoChestBlockItem extends BlockItem {

    private static final List<Component> TOOLTIP_COMPONENTS = new TooltipHintBuilder()
        .addCategory(TooltipCategoryType.WHEN_IN_INVENTORY)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_GUNS_AUTO_RELOAD_FROM_CHEST)
        .addCategory(TooltipCategoryType.WHEN_PLACED_IN_WORLD)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_NEARBY_TURRETS_USE_AMMO_FROM_CHEST)
        .build();

    public AmmoChestBlockItem() {
        super(
            AVPBlocks.AMMO_CHEST.get(),
            new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
                .component(DataComponents.MAX_STACK_SIZE, 1)
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
        tooltipComponents.addAll(TOOLTIP_COMPONENTS);
    }
}
