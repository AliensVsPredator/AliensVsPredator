package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.GOAP;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAPFactory {

    private static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        // Sensors
        .addSensor(GOAPSensors.IS_UNDERWATER)
        .addSensor(MarineGOAPSensors.BEST_ARMOR_SET)
        .addSensor(MarineGOAPSensors.BEST_WATER_BREATHING_ARMOR_SET)
        .addSensor(MarineGOAPSensors.CURRENT_ARMOR_SET)
        // Goals
        .addGoal(MarineGOAPGoals.EQUIP_BEST_ARMOR)
        .addGoal(MarineGOAPGoals.EQUIP_BEST_WATER_BREATHING_ARMOR)
        // Actions
        .addAction(MarineGOAPActions.EQUIP_BEST_ARMOR)
        .addAction(MarineGOAPActions.EQUIP_BEST_WATER_BREATHING_ARMOR)
        .build();

    public static GOAP<Marine> create() {
        return GOAP.of(GRAPH);
    }

    private MarineGOAPFactory() {
        throw new UnsupportedOperationException();
    }
}
