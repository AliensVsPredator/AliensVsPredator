package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInHandsSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInInventorySensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInWorldSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRISensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import net.minecraft.world.entity.LivingEntity;

public class FRISensors {

    public static final Sensor.Mono<LivingEntity, Option<ItemTarget.Hands<FRIStrategy>>> BEST_FRI_IN_HANDS = new Sensor.Mono.LazyCompose<>(
        BestFRIInHandsSensor.KEY,
        BestFRIInHandsSensor::sense
    );

    public static final Sensor.Mono<Marine, Option<ItemTarget.Inventory<FRIStrategy>>> BEST_FRI_IN_INVENTORY =
        new Sensor.Mono.LazyCompose<>(
            BestFRIInInventorySensor.KEY,
            BestFRIInInventorySensor::sense
        );

    public static final Sensor.Mono<LivingEntity, Option<ItemTarget.World<FRIStrategy>>> BEST_FRI_IN_WORLD = new Sensor.Mono.LazyCompose<>(
        BestFRIInWorldSensor.KEY,
        BestFRIInWorldSensor::sense
    );

    public static final Sensor.Mono<LivingEntity, Option<? extends ItemTarget<FRIStrategy>>> BEST_FRI = new Sensor.Mono.LazyCompose<>(
        BestFRISensor.KEY,
        BestFRISensor::sense
    );

    // TODO: We don't need the marine here, GOAP should support this case.
    public static final Sensor.Mono.Compose<Object, Option<? extends ItemTarget<FRIStrategy>>, ItemTarget.Location> BEST_FRI_LOCATION =
        Sensor.compose(
            BestFRISensor.KEY,
            StateKey.sensed("best_fri_location"),
            ($1, bfriOption) -> bfriOption.map(ItemTarget::location).unwrapOr(ItemTarget.Location.NONE)
        );

    public static final Sensor.Mono.Compose<LivingEntity, Option<ItemTarget.World<FRIStrategy>>, Boolean> IS_BEST_WORLD_FRI_IN_RANGE =
        Sensor
            .compose(
                BestFRIInWorldSensor.KEY,
                StateKey.sensed("is_best_world_fri_in_range"),
                (livingEntity, bfriOption) -> bfriOption.isSomeAnd(world -> livingEntity.distanceToSqr(world.itemEntity()) < 4)
            );
}
