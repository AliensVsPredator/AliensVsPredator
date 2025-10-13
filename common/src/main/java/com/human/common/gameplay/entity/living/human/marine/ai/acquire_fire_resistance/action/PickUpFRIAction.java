package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.human.common.gameplay.entity.living.human.marine.ai.action.PickUpItemAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;

public class PickUpFRIAction {

    public static Action.Signal perform(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        var worldItemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI_IN_WORLD.key(), Option.none());

        if (worldItemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return PickUpItemAction.perform(marine, worldItemTargetOption.unwrap().itemEntity());
    }

    private PickUpFRIAction() {
        throw new UnsupportedOperationException();
    }
}
