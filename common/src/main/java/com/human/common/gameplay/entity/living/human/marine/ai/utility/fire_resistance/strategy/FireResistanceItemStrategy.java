package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy;

import com.avp.common.model.inventory.AVPInventoryHolder;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.general.Strategy;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface FireResistanceItemStrategy<T extends LivingEntity & AVPInventoryHolder> extends Strategy<ItemStack, FireResistanceItemStrategy.Context<T>, Action.Result> {

    class Context<T extends LivingEntity & AVPInventoryHolder> {

        public static <T extends LivingEntity & AVPInventoryHolder> Context<T> create(T livingEntity, ReadableWorldState worldState, Blackboard blackboard, Weights weights) {
            var context = new Context<T>();
            context.apply(livingEntity, worldState, blackboard, weights);
            return context;
        }

        private Blackboard blackboard;

        private T livingEntity;

        private Weights weights;

        private ReadableWorldState worldState;

        private Context() {
        }

        public void apply(T livingEntity, ReadableWorldState worldState, Blackboard blackboard, Weights weights) {
            this.blackboard = blackboard;
            this.livingEntity = livingEntity;
            this.weights = weights;
            this.worldState = worldState;
        }

        public Blackboard getBlackboard() {
            return blackboard;
        }

        public T getLivingEntity() {
            return livingEntity;
        }

        public Weights getWeights() {
            return weights;
        }

        public float healthRatio() {
            return blackboard.getOrDefault(GOAPSensors.HEALTH_RATIO.key(), 1.0F);
        }

        public int fireResTicksRemaining() {
            return worldState.getOrDefault(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS.key(), 0);
        }

        public boolean isOnFire() {
            return worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false);
        }
    }

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
