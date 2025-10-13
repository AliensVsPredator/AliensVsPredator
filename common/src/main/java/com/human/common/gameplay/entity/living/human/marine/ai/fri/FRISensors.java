package com.human.common.gameplay.entity.living.human.marine.ai.fri;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor.BestFRIInHandsSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor.BestFRIInInventorySensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor.BestFRIInWorldSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor.BestFRISensor;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;

public class FRISensors {
    public static final Sensor.Mono<Marine, Option<ItemTarget.Hands>> BEST_FRI_IN_HANDS = new Sensor.Mono.LazyCompose<>(
        BestFRIInHandsSensor.KEY,
        BestFRIInHandsSensor::sense
    );
    public static final Sensor.Mono<Marine, Option<ItemTarget.Inventory>> BEST_FRI_IN_INVENTORY = new Sensor.Mono.LazyCompose<>(
        BestFRIInInventorySensor.KEY,
        BestFRIInInventorySensor::sense
    );
    public static final Sensor.Mono<Marine, Option<ItemTarget.World>> BEST_FRI_IN_WORLD = new Sensor.Mono.LazyCompose<>(
        BestFRIInWorldSensor.KEY,
        BestFRIInWorldSensor::sense
    );
    public static final Sensor.Mono<Marine, Option<? extends ItemTarget>> BEST_FRI = new Sensor.Mono.LazyCompose<>(
        BestFRISensor.KEY,
        BestFRISensor::sense
    );
    public static final Sensor.Mono.Compose<Marine, Option<? extends ItemTarget>, ItemTarget.Location> BEST_FRI_LOCATION = Sensor.compose(
        BestFRISensor.KEY,
        StateKey.sensed("best_fri_location"),
        ($1, bfriOption) -> bfriOption.map(ItemTarget::location).unwrapOr(ItemTarget.Location.NONE)
    );
    public static final Sensor.Mono.Compose<Marine, Option<ItemTarget.World>, Boolean> IS_BEST_WORLD_FRI_IN_RANGE = Sensor.compose(
        BestFRIInWorldSensor.KEY,
        StateKey.sensed("is_best_world_fri_in_range"),
        (marine, bfriOption) -> bfriOption.isSomeAnd(world -> marine.distanceToSqr(world.itemEntity()) < 4)
    );
}
