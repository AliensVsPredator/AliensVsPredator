package com.avp.common.entity.ai.goal;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPGoal;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;

public class PickUpFoodGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isNone()));
    }
}
