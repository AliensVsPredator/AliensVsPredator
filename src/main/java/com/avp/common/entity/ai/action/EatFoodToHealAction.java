package com.avp.common.entity.ai.action;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPAction;
import com.avp.goap.TypedIdentifier;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EatFoodToHealAction<T extends LivingEntity> extends GOAPAction<T> {

    private static final TypedIdentifier<Integer> EATING_TICK_DURATION = new TypedIdentifier<>("eatingTickDuration");

    private final float healAmount;

    public EatFoodToHealAction(float healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public GOAPWorldState createPreconditions() {
        return new GOAPWorldState(
            Map.ofEntries(
                Map.entry(GOAPConstants.HAS_FOOD, true),
                Map.entry(GOAPConstants.IS_HEALTHY, false)
            )
        );
    }

    @Override
    public GOAPWorldState createEffects() {
        return new GOAPWorldState(Map.of(GOAPConstants.IS_HEALTHY, true));
    }

    @Override
    public boolean perform(T context, GOAPBlackboard blackboard) {
        var eatingTickDuration = blackboard.getOrDefault(EATING_TICK_DURATION, 0);
        blackboard.set(EATING_TICK_DURATION, eatingTickDuration + 1);

        if (eatingTickDuration % 4 == 0) {
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
}
