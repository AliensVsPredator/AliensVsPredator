package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPSensors;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ThrowableFireResistanceItemStrategies;
import com.just.goap.Action;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class EquipThrowableResistanceItemAction {

    public static Action.@NotNull Result perform(Marine marine, ReadableWorldState worldState) {
        var bestScore = UseFireResistanceItemAction.getBestScore(
            MarineGOAPSensors.THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(),
            ThrowableFireResistanceItemStrategies::strategyFor,
            marine,
            worldState
        );

        if (bestScore == null) {
            return Action.Result.FAILED;
        }

        return EquipItemAction.perform(
            marine,
            bestScore.entry().copyItemStack(),
            InteractionHand.MAIN_HAND
        );
    }

    private EquipThrowableResistanceItemAction() {
        throw new UnsupportedOperationException();
    }
}
