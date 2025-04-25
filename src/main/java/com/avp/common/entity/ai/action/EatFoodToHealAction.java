package com.avp.common.entity.ai.action;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.util.AVPInventoryBearer;
import com.avp.goap.GOAPAction;
import com.avp.goap.TypedIdentifier;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EatFoodToHealAction<T extends LivingEntity & AVPInventoryBearer> extends GOAPAction<T> {

    private static final TypedIdentifier<Integer> EATING_TICK_DURATION = new TypedIdentifier<>("eatingTickDuration");

    private final float healAmount;

    public EatFoodToHealAction(float healAmount) {
        this.healAmount = healAmount;

        addPrecondition(GOAPConstants.COMBAT_RESPONSE, GOAPExpression.equalTo(CombatResponse.rest()));
        addPrecondition(GOAPConstants.IS_HEALTHY, GOAPExpression.isFalse());
        addPrecondition(GOAPConstants.OFF_HAND_ITEM_TYPE, GOAPExpression.equalTo(ItemType.food()));

        addEffect(new GOAPEffect.Value<>(GOAPConstants.IS_HEALTHY, true));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var eatingTickDuration = blackboard.getOrDefault(EATING_TICK_DURATION, 0);
        blackboard.set(EATING_TICK_DURATION, eatingTickDuration + 1);

        if (eatingTickDuration % 4 == 0) {
            // Throttles the eating sound so the sound isn't being spammed.
            context.playSound(SoundEvents.GENERIC_EAT);
        }

        if (eatingTickDuration > 32) {
            context.getInventory().removeItemType(context.getOffhandItem().getItem(), 1);
            context.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
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
