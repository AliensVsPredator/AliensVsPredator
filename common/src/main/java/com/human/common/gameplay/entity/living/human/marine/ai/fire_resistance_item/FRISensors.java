package com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.sensor.BestFRIInHandsSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.sensor.BestFRIInInventorySensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.sensor.BestFRIInWorldSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.sensor.BestFRISensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;

public class FRISensors {

    public static final Sensor.Mono<Marine, Option<ItemTarget.Hands<FRIStrategy>>> BEST_FRI_IN_HANDS = new Sensor.Mono.LazyCompose<>(
        BestFRIInHandsSensor.KEY,
        BestFRIInHandsSensor::sense
    );

    public static final Sensor.Mono<Marine, Option<ItemTarget.Inventory<FRIStrategy>>> BEST_FRI_IN_INVENTORY =
        new Sensor.Mono.LazyCompose<>(
            BestFRIInInventorySensor.KEY,
            BestFRIInInventorySensor::sense
        );

    public static final Sensor.Mono<Marine, Option<ItemTarget.World<FRIStrategy>>> BEST_FRI_IN_WORLD = new Sensor.Mono.LazyCompose<>(
        BestFRIInWorldSensor.KEY,
        BestFRIInWorldSensor::sense
    );

    public static final Sensor.Mono<Marine, Option<? extends ItemTarget<FRIStrategy>>> BEST_FRI = new Sensor.Mono.LazyCompose<>(
        BestFRISensor.KEY,
        BestFRISensor::sense
    );

    public static final Sensor.Mono.Compose<Marine, Option<? extends ItemTarget<FRIStrategy>>, ItemTarget.Location> BEST_FRI_LOCATION =
        Sensor.compose(
            BestFRISensor.KEY,
            StateKey.sensed("best_fri_location"),
            ($1, bfriOption) -> bfriOption.map(ItemTarget::location).unwrapOr(ItemTarget.Location.NONE)
        );

    public static final Sensor.Mono.Compose<Marine, Option<ItemTarget.World<FRIStrategy>>, Boolean> IS_BEST_WORLD_FRI_IN_RANGE = Sensor
        .compose(
            BestFRIInWorldSensor.KEY,
            StateKey.sensed("is_best_world_fri_in_range"),
            (marine, bfriOption) -> bfriOption.isSomeAnd(world -> marine.distanceToSqr(world.itemEntity()) < 4)
        );
}
