package com.lib.common.gameplay.entity.ai.goal;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPGoal;
import com.lib.common.gameplay.goap.condition.GOAPCondition;
import com.lib.common.gameplay.goap.condition.GOAPConditionContainer;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;

public class EntertainedGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.IS_BORED, GOAPExpression.isFalse()));
    }
}
