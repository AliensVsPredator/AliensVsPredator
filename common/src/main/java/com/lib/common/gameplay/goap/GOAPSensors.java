package com.lib.common.gameplay.goap;

import com.just.goap.Sensor;
import com.just.goap.StateKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.common.registry.tag.AVPBiomeTags;
import com.avp.common.util.AVPPredicates;

public class GOAPSensors {

    public static final Sensor<LivingEntity, Integer> FIRE_RESISTANCE_REMAINING_TICKS = Sensor.direct(
        StateKey.sensed("fire_resistance_remaining_ticks"),
        entity -> {
            var effect = entity.getEffect(MobEffects.FIRE_RESISTANCE);

            if (effect == null) {
                return 0;
            }

            return effect.getDuration();
        }
    );

    public static final Sensor<LivingEntity, Boolean> HAS_FIRE_RESISTANCE = Sensor.direct(
        StateKey.sensed("has_fire_resistance"),
        entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE)
    );

    public static final Sensor<LivingEntity, Boolean> HAS_WATER_BREATHING = Sensor.direct(
        StateKey.sensed("has_water_breathing"),
        entity -> entity.hasEffect(MobEffects.WATER_BREATHING)
    );

    public static final Sensor<LivingEntity, Float> HEALTH_RATIO = Sensor.direct(
        StateKey.sensed("health_ratio"),
        entity -> entity.getHealth() / entity.getMaxHealth()
    );

    public static final Sensor<Entity, Boolean> IS_ON_FIRE = Sensor.direct(StateKey.sensed("is_on_fire"), Entity::isOnFire);

    public static final Sensor<Entity, Boolean> IS_ON_GROUND = Sensor.direct(StateKey.sensed("is_on_ground"), Entity::onGround);

    public static final Sensor<Entity, Boolean> IS_UNDERWATER = Sensor.direct(StateKey.sensed("is_underwater"), Entity::isUnderWater);

    public static final Sensor<Entity, List<BlockPos>> NEARBY_BLOCK_POSITIONS = Sensor.direct(
        StateKey.sensed("nearby_block_positions"),
        entity -> BlockPos.betweenClosedStream(entity.getBoundingBox().inflate(1)).toList()
    );

    public static final Sensor<Entity, Boolean> IS_NEAR_RADIOACTIVE_BIOME = Sensor.derived(
        StateKey.sensed("is_near_radioactive_biome"),
        NEARBY_BLOCK_POSITIONS.key(),
        (entity, nearbyBlockPositions) -> nearbyBlockPositions.stream()
            .anyMatch(blockPos -> entity.level().getBiome(blockPos).is(AVPBiomeTags.IS_IRRADIATED))
    );

    public static final Sensor<Entity, List<Entity>> NEARBY_ENTITIES = Sensor.direct(
        StateKey.sensed("nearby_entities"),
        entity -> entity.level()
            .getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(16), AVPPredicates.alwaysTrue())
    );

    public static final Sensor<Entity, List<LivingEntity>> NEARBY_LIVING_ENTITIES = Sensor.derived(
        StateKey.sensed("nearby_living_entities"),
        NEARBY_ENTITIES.key(),
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> e instanceof LivingEntity)
            .map(e -> (LivingEntity) e)
            .toList()
    );

    private GOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
