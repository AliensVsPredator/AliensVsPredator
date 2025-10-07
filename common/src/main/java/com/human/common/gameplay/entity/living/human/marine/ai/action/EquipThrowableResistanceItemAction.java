package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPSensors;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ThrowableFireResistanceItemStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;

import java.util.List;

public class EquipThrowableResistanceItemAction {

    public static Action.Signal perform(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        var entries = worldState.getOrDefault(MarineGOAPSensors.THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(), List.of());

        var context = FireResistanceItemStrategy.Context.create(marine, worldState, blackboard, FireResistanceItemStrategy.Weights.DEFAULT);
        var bestItemStackResult = ThrowableFireResistanceItemStrategies.getBestStrategyAndMatchableOrNull(entries, context);

        if (bestItemStackResult == null) {
            return Action.Signal.ABORT;
        }

        return EquipItemAction.perform(marine, bestItemStackResult.matchable(), InteractionHand.MAIN_HAND);
    }

    private EquipThrowableResistanceItemAction() {
        throw new UnsupportedOperationException();
    }
}
