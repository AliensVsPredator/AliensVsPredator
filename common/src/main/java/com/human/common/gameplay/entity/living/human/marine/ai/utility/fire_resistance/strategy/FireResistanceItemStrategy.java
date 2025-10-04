package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.item.ItemStack;

public interface FireResistanceItemStrategy {

    boolean matches(ItemStack itemStack);

    double score(Marine marine, Context context, ItemStack itemStack, Weights weights);

    Action.Result consume(Marine marine, ReadableWorldState worldState, Blackboard blackboard);

    record Context(
        boolean isOnFire,
        double healthRatio,
        int fireResTicksRemaining
    ) {}

    /**
     * @param urgency             Higher means “use something now”.
     * @param duration            Reward longer duration.
     * @param sideBenefit         Reward side buffs (e.g., enchanted golden apple resistance/regen).
     * @param useTimePenalty      Penalize long use time (entity is vulnerable while using).
     * @param rarityPenalty       Make rare/valuable items harder to spend unless urgent.
     * @param overlapWastePenalty Penalize waste if the entity already has plenty of time left.
     */
    record Weights(
        double urgency,
        double duration,
        double sideBenefit,
        double useTimePenalty,
        double rarityPenalty,
        double overlapWastePenalty
    ) {

        public static final Weights DEFAULT = new Weights(
            // urgency
            2.0,
            // duration
            1.1,
            // sideBenefit
            0.8,
            // useTimePenalty
            0.9,
            // rarityPenalty
            1.2,
            // overlapWastePenalty
            1.0
        );
    }
}
