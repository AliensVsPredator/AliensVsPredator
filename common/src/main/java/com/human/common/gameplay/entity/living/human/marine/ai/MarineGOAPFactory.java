package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.GOAP;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPGoals;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAPFactory {

    private static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        // ??? Sensors
        .addSensor(GOAPSensors.IS_PROTECTED_FROM_DROWNING)
        .addSensor(GOAPSensors.IS_PROTECTED_FROM_FIRE)
        // Environment Sensors
        .addSensor(GOAPSensors.IS_NEAR_RADIOACTIVE_BIOME)
        .addSensor(GOAPSensors.IS_ON_FIRE)
        .addSensor(GOAPSensors.IS_UNDERWATER)
        .addSensor(GOAPSensors.NEARBY_BLOCK_POSITIONS)
        // Armor Sensors
        .addSensor(MarineGOAPSensors.BEST_ARMOR_SET)
        .addSensor(MarineGOAPSensors.BEST_WATER_BREATHING_ARMOR_SET)
        .addSensor(MarineGOAPSensors.CURRENT_ARMOR_SET)
        // Inventory Sensors
        .addSensor(GOAPSensors.ARMOR_ENTRIES_IN_INVENTORY)
        .addSensor(GOAPSensors.POTION_ENTRIES_IN_INVENTORY)
        .addSensor(GOAPSensors.FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY)
        .addSensor(GOAPSensors.WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY)
        // Environment Goals
        .addGoal(GOAPGoals.PREVENT_DROWNING_DAMAGE)
        .addGoal(GOAPGoals.PREVENT_FIRE_DAMAGE)
        // Armor Goals
        .addGoal(MarineGOAPGoals.EQUIP_BEST_ARMOR)
        .addGoal(MarineGOAPGoals.EQUIP_BEST_WATER_BREATHING_ARMOR)
        // Actions
        .addAction(MarineGOAPActions.DRINK_WATER_BREATHING_POTION)
        .addAction(MarineGOAPActions.DRINK_FIRE_RESISTANCE_POTION)
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
