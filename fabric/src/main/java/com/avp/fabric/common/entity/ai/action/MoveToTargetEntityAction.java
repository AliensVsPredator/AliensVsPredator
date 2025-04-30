package com.avp.fabric.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.BiPredicate;

import com.avp.fabric.goap.GOAPAction;
import com.avp.fabric.goap.TypedIdentifier;
import com.avp.fabric.goap.condition.expression.GOAPExpression;
import com.avp.fabric.goap.effect.GOAPEffect;
import com.avp.fabric.goap.state.GOAPBlackboard;
import com.avp.fabric.goap.state.GOAPWorldState;

public class MoveToTargetEntityAction<T extends Mob, E extends Entity> extends GOAPAction<T> {

    private final BiPredicate<GOAPWorldState, Double> distanceSqrIsInRangePredicate;

    private final TypedIdentifier<Option<? extends E>> nearestTargetIdentifier;

    private final double speedMultiplier;

    public MoveToTargetEntityAction(
        TypedIdentifier<Option<? extends E>> nearestTargetIdentifier,
        TypedIdentifier<Boolean> isInRangeIdentifier,
        double speedMultiplier,
        BiPredicate<GOAPWorldState, Double> distanceSqrIsInRangePredicate
    ) {
        this.distanceSqrIsInRangePredicate = distanceSqrIsInRangePredicate;
        this.nearestTargetIdentifier = nearestTargetIdentifier;
        this.speedMultiplier = speedMultiplier;

        addPrecondition(nearestTargetIdentifier, GOAPExpression.isSome());
        addPrecondition(isInRangeIdentifier, GOAPExpression.isFalse());

        addEffect(new GOAPEffect.Value<>(isInRangeIdentifier, true));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var targetEntityOption = worldState.getOrDefault(nearestTargetIdentifier, Option.none());

        if (targetEntityOption.isNone()) {
            return true;
        }

        var target = targetEntityOption.unwrap();

        if (distanceSqrIsInRangePredicate.test(worldState, context.distanceToSqr(target))) {
            return true;
        }

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
        return worldState.getOrDefault(nearestTargetIdentifier, Option.none())
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
