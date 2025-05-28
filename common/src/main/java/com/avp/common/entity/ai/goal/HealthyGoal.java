package com.avp.common.entity.ai.goal;

import com.xlib.goap.GOAPGoal;
import com.xlib.goap.condition.GOAPCondition;
import com.xlib.goap.condition.GOAPConditionContainer;
import com.xlib.goap.condition.expression.GOAPExpression;

import com.avp.common.entity.ai.GOAPConstants;

public class HealthyGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.IS_HEALTHY, GOAPExpression.isTrue()));
    }
}
