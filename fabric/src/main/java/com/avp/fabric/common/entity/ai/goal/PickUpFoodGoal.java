package com.avp.fabric.common.entity.ai.goal;

import com.avp.common.goap.GOAPGoal;
import com.avp.common.goap.condition.GOAPCondition;
import com.avp.common.goap.condition.GOAPConditionContainer;
import com.avp.common.goap.condition.expression.GOAPExpression;
import com.avp.fabric.common.entity.ai.GOAPConstants;

public class PickUpFoodGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isNone()));
    }
}
