package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRIActions;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRIGoals;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.ExtinguishFireGoals;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.ExtinguishFireSensors;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAP {

    public static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        .apply(MarineGOAP::addExtinguishSelfPackage)
        .apply(MarineGOAP::addAcquireFireResistancePackage)
        .build();

    // TODO:
    private static void addExtinguishSelfPackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder.addGoal(ExtinguishFireGoals.EXTINGUISH_SELF_GOAL);

        graphBuilder.addAction(MarineGOAPActions.EQUIP_WATER_BUCKET_ACTION);
        graphBuilder.addAction(MarineGOAPActions.PLACE_WATER_AT_FEET_ACTION);

        graphBuilder.addSensor(GOAPSensors.HAS_FIRE_RESISTANCE);
        graphBuilder.addSensor(ExtinguishFireSensors.HAS_WATER_BUCKET_EQUIPPED);
        graphBuilder.addSensor(ExtinguishFireSensors.WATER_BUCKET_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_GROUND);

        // For water bucket usage validity.
        graphBuilder.addSensor(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE);
    }

    private static void addAcquireFireResistancePackage(Graph.Builder<Marine> graphBuilder) {
        // The goal we want to complete.
        graphBuilder.addGoal(FRIGoals.ACQUIRE_FIRE_RESISTANCE_GOAL);

        // Actions that can complete the goal.
        graphBuilder.addAction(FRIActions.MOVE_TO_BEST_FRI);
        graphBuilder.addAction(FRIActions.pickUpBestFRIFactory());
        graphBuilder.addAction(FRIActions.equipBestFRIFactory());
        graphBuilder.addAction(FRIActions.USE_BEST_FRI);

        // General usage.
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.HAS_FIRE_RESISTANCE);
        // Used for locating best FRI.
        graphBuilder.addSensor(FRISensors.BEST_FRI);
        graphBuilder.addSensor(FRISensors.BEST_FRI_LOCATION);
        // Used for locating best FRI on self.
        graphBuilder.addSensor(FRISensors.BEST_FRI_IN_HANDS);
        graphBuilder.addSensor(FRISensors.BEST_FRI_IN_INVENTORY);
        // Used for locating best FRI in world.
        graphBuilder.addSensor(FRISensors.BEST_FRI_IN_WORLD);
        graphBuilder.addSensor(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE);
        graphBuilder.addSensor(GOAPSensors.NEARBY_ENTITIES);
        graphBuilder.addSensor(GOAPSensors.NEARBY_ITEM_ENTITIES);
        // Used for utility scoring.
        graphBuilder.addSensor(GOAPSensors.HEALTH_RATIO);
        graphBuilder.addSensor(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS);

        // For throwable potion usage validity.
        graphBuilder.addSensor(GOAPSensors.IS_ON_GROUND);
    }

    public static void initialize() {}

    private MarineGOAP() {
        throw new UnsupportedOperationException();
    }
}
