package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.just.goap.graph.Graph;

public class OvomorphGOAP {

    public static final Graph<Ovomorph> GRAPH = Graph.<Ovomorph>builder()
        // Sensors
        .addSensor(OvomorphGOAPSensors.HATCH_STATE)
        .addSensor(OvomorphGOAPSensors.WANTS_TO_HATCH)
        // Goals
        .addGoal(OvomorphGOAPGoals.HATCH)
        // Actions
        .addAction(OvomorphGOAPActions.HATCH)
        .build();

    private OvomorphGOAP() {
        throw new UnsupportedOperationException();
    }
}
