package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPGoals;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAP {

    public static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        .apply(MarineGOAP::addGenericPackage)
        .apply(MarineGOAP::addHealthPackage)
        .apply(MarineGOAP::addDrowningPreventionPackage)
        .apply(MarineGOAP::addFirePreventionPackage)
        .build();

    private static void addGenericPackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder
            .addSensor(GOAPSensors.IS_NEAR_RADIOACTIVE_BIOME)
            .addSensor(GOAPSensors.IS_ON_GROUND)
            .addSensor(GOAPSensors.NEARBY_BLOCK_POSITIONS)
            // Armor Sensors
            .addSensor(MarineGOAPSensors.BEST_ARMOR_SET)
            .addSensor(MarineGOAPSensors.BEST_WATER_BREATHING_ARMOR_SET)
            .addSensor(MarineGOAPSensors.CURRENT_ARMOR_SET)
            // Inventory Sensors
            .addSensor(GOAPSensors.ARMOR_ENTRIES_IN_INVENTORY)
            .addSensor(GOAPSensors.POTION_ENTRIES_IN_INVENTORY)
            // Armor Goals
            .addGoal(MarineGOAPGoals.EQUIP_BEST_ARMOR)
            .addGoal(MarineGOAPGoals.EQUIP_BEST_WATER_BREATHING_ARMOR)
            // Actions
            .addAction(MarineGOAPActions.EQUIP_BEST_ARMOR)
            .addAction(MarineGOAPActions.EQUIP_BEST_WATER_BREATHING_ARMOR);
    }

    private static void addHealthPackage(Graph.Builder<Marine> graphBuilder) {
        // Goals
        graphBuilder.addGoal(GOAPGoals.KEEP_HEALTH_UP);
        // Actions
        graphBuilder.addAction(MarineGOAPActions.DRINK_INSTANT_HEALTH_POTION);
        // Sensors
        graphBuilder.addSensor(GOAPSensors.IS_FULL_HEALTH);
    }

    private static void addDrowningPreventionPackage(Graph.Builder<Marine> graphBuilder) {
        // Goals
        graphBuilder.addGoal(GOAPGoals.PREVENT_DROWNING_DAMAGE);
        // Actions
        graphBuilder.addAction(MarineGOAPActions.DRINK_WATER_BREATHING_POTION);
        // TODO: Add "equip water breathing armor set" action
        // TODO: Add "equip turtle helmet" action
        // TODO: Add "equip respiration helmet" action
        // Sensors
        graphBuilder.addSensor(GOAPSensors.IS_UNDERWATER);
        graphBuilder.addSensor(GOAPSensors.IS_PROTECTED_FROM_DROWNING);
    }

    private static void addFirePreventionPackage(Graph.Builder<Marine> graphBuilder) {
        // Goals
        graphBuilder.addGoal(GOAPGoals.PREVENT_FIRE_DAMAGE);
        // Actions
        graphBuilder.addAction(MarineGOAPActions.DRINK_FIRE_RESISTANCE_POTION);
        // Sensors
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.IS_PROTECTED_FROM_FIRE);
    }

    private MarineGOAP() {
        throw new UnsupportedOperationException();
    }
}
