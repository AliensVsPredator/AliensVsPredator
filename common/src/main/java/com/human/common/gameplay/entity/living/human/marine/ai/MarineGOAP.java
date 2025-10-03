package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAP {

    public static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        .apply(MarineGOAP::addGenericPackage)
        .apply(MarineGOAP::addFirePreventionPackage)
        .build();

    private static void addGenericPackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder
            // Inventory Sensors
            .addSensor(GOAPSensors.ARMOR_ENTRIES_IN_INVENTORY)
            .addSensor(GOAPSensors.POTION_ENTRIES_IN_INVENTORY);
    }

    private static void addFirePreventionPackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder.addGoal(MarineGOAPGoals.EXTINGUISH_SELF_GOAL);

        graphBuilder.addAction(MarineGOAPActions.PLACE_WATER_AT_FEET_ACTION);

        graphBuilder.addSensor(GOAPSensors.HAS_FIRE_RESISTANCE);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_WATER_BUCKET);
        graphBuilder.addSensor(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_GROUND);
    }

    private MarineGOAP() {
        throw new UnsupportedOperationException();
    }
}
