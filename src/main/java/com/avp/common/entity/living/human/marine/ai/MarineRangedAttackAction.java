package com.avp.common.entity.living.human.marine.ai;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.action.RangedAttackAction;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.item.GunItem;
import com.avp.goap.TypedIdentifier;
import com.avp.goap.state.GOAPBlackboard;

public class MarineRangedAttackAction extends RangedAttackAction<Marine> {

    private static final TypedIdentifier<Integer> COOLDOWN = new TypedIdentifier<>("cooldown");

    // Controls sound queues and other time-based gun effects.
    private static final TypedIdentifier<Integer> TICK_PROGRESS = new TypedIdentifier<>("tickProgress");

    @Override
    protected void performRangedAttack(Marine context, LivingEntity target, GOAPBlackboard blackboard) {
        var itemStack = context.getMainHandItem();

        if (itemStack.getItem() instanceof GunItem gunItem) {
            var fireMode = gunItem.gunConfig().getDefaultFireMode();
            var cooldownTicks = fireMode.cooldownInTicks();
            // Decrement cooldown on every access/tick.
            var currentCooldown = blackboard.getOrDefault(COOLDOWN, 0) - 1;
            // Get current tick progress.
            var tickProgress = blackboard.getOrDefault(TICK_PROGRESS, 0);

            blackboard.set(COOLDOWN, currentCooldown);

            if (currentCooldown <= 0) {
                gunItem.tryShoot(context, itemStack, fireMode, tickProgress, tickProgress);
                blackboard.set(COOLDOWN, cooldownTicks);
            }

            blackboard.set(TICK_PROGRESS, tickProgress + 1);
        }
    }
}
