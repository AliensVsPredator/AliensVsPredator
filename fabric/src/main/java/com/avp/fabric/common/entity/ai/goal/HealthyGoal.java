package com.avp.fabric.common.entity.ai.goal;

import com.avp.fabric.common.entity.ai.GOAPConstants;
import com.avp.fabric.goap.GOAPGoal;
import com.avp.fabric.goap.condition.GOAPCondition;
import com.avp.fabric.goap.condition.GOAPConditionContainer;
import com.avp.fabric.goap.condition.expression.GOAPExpression;

public class HealthyGoal extends GOAPGoal {

    @Override
    public GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(new GOAPCondition<>(GOAPConstants.IS_HEALTHY, GOAPExpression.isTrue()));
    }
}
