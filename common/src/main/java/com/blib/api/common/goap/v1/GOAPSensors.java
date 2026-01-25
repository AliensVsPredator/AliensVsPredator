package com.blib.api.common.goap.v1;

import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;

import com.blib.api.common.entity.v1.BLibEntityPredicates;

public class GOAPSensors {

    public static final Sensor.Mono<LivingEntity, Integer> FIRE_RESISTANCE_REMAINING_TICKS = Sensors.map(
        StateKey.sensed("fire_resistance_remaining_ticks"),
        entity -> {
            var effect = entity.getEffect(MobEffects.FIRE_RESISTANCE);

            if (effect == null) {
                return 0;
            }

            return effect.getDuration();
        }
    );

    public static final Sensor.Mono<LivingEntity, Boolean> HAS_FIRE_RESISTANCE = Sensors.map(
        StateKey.sensed("has_fire_resistance"),
        entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE)
    );

    public static final Sensor.Mono<LivingEntity, Boolean> HAS_WATER_BREATHING = Sensors.map(
        StateKey.sensed("has_water_breathing"),
        entity -> entity.hasEffect(MobEffects.WATER_BREATHING)
    );

    public static final Sensor.Mono<LivingEntity, Float> HEALTH_RATIO = Sensors.map(
        StateKey.sensed("health_ratio"),
        entity -> entity.getHealth() / entity.getMaxHealth()
    );

    public static final Sensor.Mono<Entity, Boolean> IS_IN_LAVA = Sensors.map(StateKey.sensed("is_in_lava"), Entity::isInLava);

    public static final Sensor.Mono<Entity, Boolean> IS_ON_FIRE = Sensors.map(StateKey.sensed("is_on_fire"), Entity::isOnFire);

    public static final Sensor.Mono<Entity, Boolean> IS_ON_GROUND = Sensors.map(StateKey.sensed("is_on_ground"), Entity::onGround);

    public static final Sensor.Mono<Entity, Boolean> IS_UNDERWATER = Sensors.map(StateKey.sensed("is_underwater"), Entity::isUnderWater);

    public static final Sensor.Mono<Entity, List<BlockPos>> NEARBY_BLOCK_POSITIONS = Sensors.map(
        StateKey.sensed("nearby_block_positions"),
        entity -> BlockPos.betweenClosedStream(entity.blockPosition().offset(-1, 0, -1), entity.blockPosition().offset(1, 0, 1)).toList()
    );

    public static final Sensor.Mono<Entity, List<Entity>> NEARBY_ENTITIES = Sensors.map(
        StateKey.sensed("nearby_entities"),
        entity -> entity.level()
            .getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(16), BLibEntityPredicates.alwaysTrue())
    );

    public static final Sensor.Mono<Entity, List<LivingEntity>> NEARBY_LIVING_ENTITIES = Sensors.compose(
        NEARBY_ENTITIES.key(),
        StateKey.sensed("nearby_living_entities"),
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> e instanceof LivingEntity)
            .map(e -> (LivingEntity) e)
            .toList()
    );

    public static final Sensor.Mono<Entity, List<ItemEntity>> NEARBY_ITEM_ENTITIES = Sensors.compose(
        NEARBY_ENTITIES.key(),
        StateKey.sensed("nearby_item_entities"),
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> e instanceof ItemEntity)
            .map(e -> (ItemEntity) e)
            .toList()
    );

    private GOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
