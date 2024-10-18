package org.avp.api.common.ai.sensor.key;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;

public class SensorKeys {

    public static final SensorKey<List<Entity>> NEARBY_ENTITIES = new SensorKey<>("nearby_entities");

    public static final SensorKey<List<LivingEntity>> NEARBY_LIVING_ENTITIES = new SensorKey<>("nearby_living_entities");

    public static final SensorKey<List<ItemEntity>> NEARBY_ITEM_ENTITIES = new SensorKey<>("nearby_item_entities");

    public static final SensorKey<Entity> TARGET = new SensorKey<>("target");

    private SensorKeys() {
        throw new UnsupportedOperationException();
    }
}
