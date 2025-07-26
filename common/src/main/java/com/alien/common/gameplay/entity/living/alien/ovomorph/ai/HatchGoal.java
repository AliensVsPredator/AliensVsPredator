package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.model.alien.HatchState;
import com.lib.common.gameplay.goap.GOAPGoal;
import com.lib.common.gameplay.goap.condition.GOAPCondition;
import com.lib.common.gameplay.goap.condition.GOAPConditionContainer;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;

public class HatchGoal extends GOAPGoal {

    @Override
    protected GOAPConditionContainer createDesiredConditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(OvomorphGOAP.HATCH_STATE, GOAPExpression.equalTo(HatchState.HATCHED))
        );
    }
}
