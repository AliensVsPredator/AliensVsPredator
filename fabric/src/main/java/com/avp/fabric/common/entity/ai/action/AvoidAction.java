package com.avp.fabric.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;

import com.avp.fabric.common.entity.ai.GOAPConstants;
import com.avp.fabric.common.entity.ai.util.CombatResponse;
import com.avp.fabric.goap.GOAPAction;
import com.avp.fabric.goap.TypedIdentifier;
import com.avp.fabric.goap.condition.expression.GOAPExpression;
import com.avp.fabric.goap.effect.GOAPEffect;
import com.avp.fabric.goap.state.GOAPBlackboard;
import com.avp.fabric.goap.state.GOAPWorldState;

public class AvoidAction<T extends PathfinderMob> extends GOAPAction<T> {

    private static final TypedIdentifier<Path> AVOID_PATH = new TypedIdentifier<>("avoidPath");

    private static final TypedIdentifier<BlockPos> AVOID_PATH_TARGET_POS = new TypedIdentifier<>("avoidPathTargetPos");

    private final int avoidRange;

    private final float avoidSpeedModifier;

    public AvoidAction(int avoidRange, float avoidSpeedModifier) {
        this.avoidRange = avoidRange;
        this.avoidSpeedModifier = avoidSpeedModifier;

        addPrecondition(GOAPConstants.COMBAT_RESPONSE, GOAPExpression.equalTo(CombatResponse.flight()));
        addPrecondition(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, GOAPExpression.isSome());
        addPrecondition(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, GOAPExpression.isTrue());

        addEffect(new GOAPEffect.Value<>(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none()));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var targetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        if (targetOption.isNone()) {
            // Target does not exist, mission success.
            return true;
        }

        var target = targetOption.unwrap();

        if (
            // Target is dead, nothing to avoid...
            target.isDeadOrDying()
                // OR target is far enough away, no sense trying to avoid.
                || context.distanceToSqr(target) > avoidRange * avoidRange
        ) {
            // Target is dead, dying, or far enough away. Mission success.
            return true;
        }

        getOrCreateAvoidPath(context, target, blackboard)
            .ifSome(avoidPath -> context.getNavigation().moveTo(avoidPath, avoidSpeedModifier));

        return context.getNavigation().isDone();
    }

    @Override
    public void onFinish(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        // Stop moving away from avoid target.
        context.getNavigation().stop();
    }

    private Option<Path> getOrCreateAvoidPath(T context, LivingEntity toAvoid, GOAPBlackboard blackboard) {
        var currentAvoidPath = blackboard.get(AVOID_PATH);
        var cachedTargetPos = blackboard.get(AVOID_PATH_TARGET_POS);
        var currentTargetPos = toAvoid.blockPosition();

        if (currentAvoidPath != null && cachedTargetPos != null && cachedTargetPos.equals(currentTargetPos)) {
            return Option.some(currentAvoidPath);
        }

        var vec3 = DefaultRandomPos.getPosAway(context, avoidRange + (avoidRange / 2), 7, toAvoid.position());

        if (vec3 == null) {
            return Option.none();
        } else if (toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid.distanceToSqr(context)) {
            return Option.none();
        } else {
            var avoidPath = context.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);

            if (avoidPath != null) {
                blackboard.set(AVOID_PATH, avoidPath);
                blackboard.set(AVOID_PATH_TARGET_POS, currentTargetPos);
            }

            return Option.ofNullable(avoidPath);
        }
    }
}
