package com.avp.common.item;

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

import com.avp.common.component.DataComponents;

public class MilkCanisterItem extends Item {

    public MilkCanisterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int currentContentAmount = stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);
        if (currentContentAmount == 0)
            return;

        tooltipComponents.add(
            Component.translatable("tooltip.avp.capacity").append(currentContentAmount + "/" + CanisterItem.MAX_CONTENT_AMOUNT)
        );
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide)
            livingEntity.removeAllEffects();

        if (livingEntity instanceof Player player) {
            if (stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) > 1 && !player.isCreative())
                return CanisterItem.updateContentAmount(stack, -1);

            return ItemUtils.createFilledResult(stack, player, new ItemStack(AVPItems.CANISTER), false);

        } else {
            if (stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) > 1)
                return CanisterItem.updateContentAmount(stack, -1);

            stack.consume(1, livingEntity);
            return stack;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }
}
