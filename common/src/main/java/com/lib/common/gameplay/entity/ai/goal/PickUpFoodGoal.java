package com.lib.common.gameplay.entity.ai.goal;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPGoal;
import com.lib.common.gameplay.goap.condition.GOAPCondition;
import com.lib.common.gameplay.goap.condition.GOAPConditionContainer;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;

public class PickUpFoodGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isNone()));
    }
}
