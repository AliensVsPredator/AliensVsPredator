package com.avp.common.entity.ai.action;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPAction;
import com.avp.goap.TypedIdentifier;
import com.avp.goap.expression.GOAPCondition;
import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.expression.GOAPExpression;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EatFoodToHealAction<T extends LivingEntity> extends GOAPAction<T> {

    private static final TypedIdentifier<Integer> EATING_TICK_DURATION = new TypedIdentifier<>("eatingTickDuration");

    private final float healAmount;

    public EatFoodToHealAction(float healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public GOAPConditionSet createPreconditions() {
        return GOAPConditionSet.of(
            new GOAPCondition<>(GOAPConstants.HAS_FOOD_IN_INVENTORY, GOAPExpression.isTrue()),
            new GOAPCondition<>(GOAPConstants.IS_HEALTHY, GOAPExpression.isFalse())
        );
    }

    @Override
    public GOAPWorldState createEffects() {
        return new GOAPWorldState(Map.of(GOAPConstants.IS_HEALTHY, true));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var eatingTickDuration = blackboard.getOrDefault(EATING_TICK_DURATION, 0);
        blackboard.set(EATING_TICK_DURATION, eatingTickDuration + 1);

        if (eatingTickDuration % 4 == 0) {
            // Throttles the eating sound so the sound isn't being spammed.
            context.playSound(SoundEvents.GENERIC_EAT);
        }

        if (eatingTickDuration > 20) {
            // TODO: Make the entity eat food from its inventory.
            context.setHealth(context.getHealth() + healAmount);
            return true;
        }

        // Not finished eating, yet.
        return false;
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return context.getHealth() / context.getMaxHealth();
    }
}
