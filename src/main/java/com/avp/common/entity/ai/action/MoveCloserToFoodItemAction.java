package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Mob;

import java.util.Map;
import java.util.function.BiPredicate;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPAction;
import com.avp.goap.expression.GOAPCondition;
import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.expression.GOAPExpression;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class MoveCloserToFoodItemAction<T extends Mob> extends GOAPAction<T> {

    private final BiPredicate<T, Double> distanceSqrToTargetPredicate;

    public MoveCloserToFoodItemAction(BiPredicate<T, Double> distanceSqrToTargetPredicate) {
        this.distanceSqrToTargetPredicate = distanceSqrToTargetPredicate;
    }

    @Override
    public GOAPConditionSet createPreconditions() {
        return GOAPConditionSet.of(
            new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isSome()),
            new GOAPCondition<>(GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE, GOAPExpression.isFalse())
        );
    }

    @Override
    public GOAPWorldState createEffects() {
        return new GOAPWorldState(Map.of(GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE, true));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var targetEntityOption = worldState.getOrDefault(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none());

        if (targetEntityOption.isNone()) {
            // TODO: Don't return true here but return "impossible" or "invalid" or something.
            return true;
        }

        var target = targetEntityOption.unwrap();

        if (distanceSqrToTargetPredicate.test(context, context.distanceToSqr(target))) {
            return true;
        }

        var targetPos = target.blockPosition().below();
        var state = context.level().getBlockState(targetPos);

        // Prevents entity from jumping off of ledges to go after the target.
        if (state.entityCanStandOn(context.level(), targetPos, context)) {
            context.getNavigation().moveTo(target, 1.0);
        }

        return false;
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        var targetOption = worldState.getOrDefault(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none());
        // TODO:
        return 0.5F;
    }
}
