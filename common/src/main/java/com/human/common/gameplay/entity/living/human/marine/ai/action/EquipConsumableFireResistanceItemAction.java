package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPSensors;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ConsumableFireResistanceItemStrategies;
import com.just.goap.Action;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class EquipConsumableFireResistanceItemAction {

    public static Action.@NotNull Result perform(Marine marine, ReadableWorldState worldState) {
        var bestScore = UseFireResistanceItemAction.getBestScore(
            MarineGOAPSensors.CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(),
            ConsumableFireResistanceItemStrategies::strategyFor,
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

    private EquipConsumableFireResistanceItemAction() {
        throw new UnsupportedOperationException();
    }
}
