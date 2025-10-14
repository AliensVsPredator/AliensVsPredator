package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.human.common.gameplay.entity.living.human.marine.ai.action.PickUpItemAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class PickUpFRIAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var worldItemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI_IN_WORLD.key(), Option.none());

        if (worldItemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return PickUpItemAction.perform(livingEntityWithInventory, worldItemTargetOption.unwrap().itemEntity());
    }

    private PickUpFRIAction() {
        throw new UnsupportedOperationException();
    }
}
