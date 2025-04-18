package com.avp.common.entity.ai.goal;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPGoal;
import com.avp.goap.expression.GOAPCondition;
import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.expression.GOAPExpression;

public class HealthyGoal extends GOAPGoal {

    @Override
    public GOAPConditionSet createDesiredConditions() {
        return GOAPConditionSet.of(new GOAPCondition<>(GOAPConstants.IS_HEALTHY, GOAPExpression.isTrue()));
    }
}
