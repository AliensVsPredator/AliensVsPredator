package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.just.goap.GOAP;
import com.just.goap.graph.Graph;

public class OvomorphGOAPFactory {

    private static final Graph<Ovomorph> GRAPH = Graph.<Ovomorph>builder()
        // Sensors
        .addSensor(OvomorphGOAPSensors.HATCH_STATE)
        .addSensor(OvomorphGOAPSensors.WANTS_TO_HATCH)
        // Goals
        .addGoal(OvomorphGOAPGoals.HATCH)
        // Actions
        .addAction(OvomorphGOAPActions.HATCH)
        .build();

    public static GOAP<Ovomorph> create() {
        return GOAP.of(GRAPH);
    }

    private OvomorphGOAPFactory() {
        throw new UnsupportedOperationException();
    }
}
