package com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.FRISensors;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;

public class EquipFRIAction {

    public static Action.Signal perform(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        var inventoryItemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI_IN_INVENTORY.key(), Option.none());

        if (inventoryItemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        return EquipItemAction.perform(marine, inventoryItemTargetOption.unwrap().entry(), InteractionHand.MAIN_HAND);
    }

    private EquipFRIAction() {
        throw new UnsupportedOperationException();
    }
}
