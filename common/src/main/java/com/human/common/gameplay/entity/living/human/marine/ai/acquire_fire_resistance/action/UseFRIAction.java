package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class UseFRIAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntity,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var itemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI.key(), Option.none());

        if (itemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return itemTargetOption.unwrap().strategy().execute(livingEntity, worldState, blackboard);
    }

    private UseFRIAction() {
        throw new UnsupportedOperationException();
    }
}
