package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class MoveCloserToAttackTargetEntityAction<T extends Mob> extends GOAPAction<T> {

    private final double speedMultiplier;

    public MoveCloserToAttackTargetEntityAction(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, GOAPExpression.isSome()),
            new GOAPCondition<>(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, GOAPExpression.isFalse())
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, true)
        );
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var targetEntityOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        if (targetEntityOption.isNone()) {
            return true;
        }

        var target = targetEntityOption.unwrap();

        var targetPos = target.blockPosition().below();
        var state = context.level().getBlockState(targetPos);

        // Prevents entity from jumping off of ledges to go after the target.
        if (state.entityCanStandOn(context.level(), targetPos, context)) {
            context.getNavigation().moveTo(target, speedMultiplier);
        }

        return false;
    }

    @Override
    public void onFinish(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        // Stop moving if target is now in range.
        context.getNavigation().stop();
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none())
            .match(
                target -> {
                    var distSqr = (float) context.distanceToSqr(target);
                    var attribute = context.getAttribute(Attributes.FOLLOW_RANGE);
                    // TODO: Use constant for default max distance value here.
                    var maxDistance = attribute == null ? 16F : (float) attribute.getValue();
                    return Math.clamp(distSqr / (maxDistance * maxDistance), 0F, 1F);
                },
                () -> 1F
            );
    }
}
