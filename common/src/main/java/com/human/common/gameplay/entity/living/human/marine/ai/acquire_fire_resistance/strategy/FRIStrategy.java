package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy;

import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

import com.avp.common.model.inventory.AVPInventory;

public interface FRIStrategy {

    boolean canUseItemStack(ItemStack itemStack);

    boolean isValid(LivingEntity livingEntity, ReadableWorldState worldState);

    Collection<AVPInventory.Entry> selectEntriesFromInventory(AVPInventory inventory);

    double score(LivingEntity livingEntity, ReadableWorldState worldState, ItemStack itemStack);

    Action.Signal execute(LivingEntity livingEntity, ReadableWorldState worldState, Blackboard blackboard);

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
