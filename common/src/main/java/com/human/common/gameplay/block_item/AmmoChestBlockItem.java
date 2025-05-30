package com.human.common.gameplay.block_item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.registry.init.AVPBlocks;

public class AmmoChestBlockItem extends BlockItem {

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

        tooltipComponents.add(
            Component.translatable("tooltip.avp.ammo_chest.in_inventory")
                .withStyle(ChatFormatting.YELLOW)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.ammo_chest.reload_from_chest")
                .withStyle(ChatFormatting.GREEN)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.ammo_chest.placed")
                .withStyle(ChatFormatting.YELLOW)
        );
        tooltipComponents.add(
            Component.translatable("tooltip.avp.ammo_chest.turret_load_from_chest")
                .withStyle(ChatFormatting.GREEN)
        );
    }
}
