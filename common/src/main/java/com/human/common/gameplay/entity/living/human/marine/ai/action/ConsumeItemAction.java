package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.just.goap.Action;
import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class ConsumeItemAction {

    private static final StateKey<Integer> CONSUME_TICK_DURATION = StateKey.sensed("consume_tick_duration");

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Result perform(
        SoundEvent soundEvent,
        T entity,
        Blackboard blackboard,
        Supplier<Action.Result> onConsume
    ) {
        var tickDuration = blackboard.getOrDefault(CONSUME_TICK_DURATION, 0);
        blackboard.set(CONSUME_TICK_DURATION, tickDuration + 1);

        if (tickDuration >= 32) {
            return onConsume.get();
        }

        if (tickDuration % 4 == 0) {
            // Throttles sound so the sound isn't being spammed.
            entity.playSound(soundEvent, 1F, entity.level().random.nextFloat() * 0.1F + 0.9F);
        }

        // Not finished, yet.
        return Action.Result.CONTINUE;
    }
}
