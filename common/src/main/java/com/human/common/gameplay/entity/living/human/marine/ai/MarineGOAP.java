package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.graph.Graph;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAP {

    public static final Graph<Marine> GRAPH = Graph.<Marine>builder()
        .apply(MarineGOAP::addExtinguishSelfPackage)
        .apply(MarineGOAP::addAcquireFireResistancePackage)
        .build();

    private static void addExtinguishSelfPackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder.addGoal(MarineGOAPGoals.EXTINGUISH_SELF_GOAL);

        graphBuilder.addAction(MarineGOAPActions.EQUIP_WATER_BUCKET_ACTION);
        graphBuilder.addAction(MarineGOAPActions.PLACE_WATER_AT_FEET_ACTION);

        graphBuilder.addSensor(GOAPSensors.HAS_FIRE_RESISTANCE);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_WATER_BUCKET_EQUIPPED);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_WATER_BUCKET_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_GROUND);
    }

    private static void addAcquireFireResistancePackage(Graph.Builder<Marine> graphBuilder) {
        graphBuilder.addGoal(MarineGOAPGoals.ACQUIRE_FIRE_RESISTANCE_GOAL);

        graphBuilder.addAction(MarineGOAPActions.EQUIP_CONSUMABLE_FIRE_RESISTANCE_ITEM);
        graphBuilder.addAction(MarineGOAPActions.CONSUME_FIRE_RESISTANCE_ITEM);

        graphBuilder.addAction(MarineGOAPActions.EQUIP_THROWABLE_FIRE_RESISTANCE_ITEM);
        graphBuilder.addAction(MarineGOAPActions.THROW_FIRE_RESISTANCE_ITEM);

        graphBuilder.addSensor(MarineGOAPSensors.CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_CONSUMABLE_FIRE_RESISTANCE_ITEM_EQUIPPED);

        graphBuilder.addSensor(MarineGOAPSensors.THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY);
        graphBuilder.addSensor(MarineGOAPSensors.HAS_THROWABLE_FIRE_RESISTANCE_ITEM_EQUIPPED);

        graphBuilder.addSensor(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS);
        graphBuilder.addSensor(GOAPSensors.HAS_FIRE_RESISTANCE);
        graphBuilder.addSensor(GOAPSensors.HEALTH_RATIO);
        graphBuilder.addSensor(GOAPSensors.IS_ON_FIRE);
        graphBuilder.addSensor(GOAPSensors.IS_ON_GROUND);
    }

    private MarineGOAP() {
        throw new UnsupportedOperationException();
    }
}
