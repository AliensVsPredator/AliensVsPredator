package com.human.common.gameplay.item.canister;

import com.human.common.registry.init.HumanDataComponents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.registry.init.item.AVPItems;

public class MilkCanisterItem extends Item {

    public MilkCanisterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @NotNull TooltipContext context,
        @NotNull List<Component> tooltipComponents,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int currentContentAmount = stack.getOrDefault(HumanDataComponents.CANISTER_CAPACITY.get(), 0);

        if (currentContentAmount == 0) {
            return;
        }

        tooltipComponents.add(
            Component.translatable("tooltip.avp.capacity").append(currentContentAmount + "/" + CanisterItem.MAX_CAPACITY)
        );
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            livingEntity.removeAllEffects();
        }

        var canDeplete = stack.getOrDefault(HumanDataComponents.CANISTER_CAPACITY.get(), 0) > 1;

        if (livingEntity instanceof Player player) {
            if (canDeplete && !player.isCreative()) {
                return CanisterItem.updateCapacity(player, stack, -1);
            }

            return ItemUtils.createFilledResult(stack, player, new ItemStack(AVPItems.CANISTER.get()), false);
        } else {
            if (canDeplete) {
                return CanisterItem.updateCapacity(livingEntity, stack, -1);
            }

            stack.consume(1, livingEntity);
            return stack;
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand usedHand
    ) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }
}
