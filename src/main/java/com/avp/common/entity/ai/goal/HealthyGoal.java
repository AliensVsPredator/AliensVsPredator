package com.avp.common.entity.ai.goal;

import java.util.Map;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPGoal;
import com.avp.goap.state.GOAPWorldState;

public class HealthyGoal extends GOAPGoal {

    @Override
    public GOAPWorldState createDesiredWorldState() {
        return new GOAPWorldState(Map.of(GOAPConstants.IS_HEALTHY, true));
    }
}
