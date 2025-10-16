package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInHandsSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInInventorySensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRIInWorldSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor.BestFRISensor;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategyResult;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.sensor.Compose;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import net.minecraft.world.entity.LivingEntity;

public class FRISensors {

    public static final Sensor.Mono<LivingEntity, Option<FRIStrategyResult<ItemTarget.Equipped>>> BEST_FRI_IN_HANDS = Sensors.lazyCompose(
        BestFRIInHandsSensor.KEY,
        BestFRIInHandsSensor::sense
    );

    public static final Sensor.Mono<Marine, Option<FRIStrategyResult<ItemTarget.Inventory>>> BEST_FRI_IN_INVENTORY =
        Sensors.lazyCompose(
            BestFRIInInventorySensor.KEY,
            BestFRIInInventorySensor::sense
        );

    public static final Sensor.Mono<LivingEntity, Option<FRIStrategyResult<ItemTarget.World>>> BEST_FRI_IN_WORLD = Sensors.lazyCompose(
        BestFRIInWorldSensor.KEY,
        BestFRIInWorldSensor::sense
    );

    public static final Sensor.Mono<LivingEntity, Option<FRIStrategyResult<? extends ItemTarget>>> BEST_FRI = Sensors.lazyCompose(
        BestFRISensor.KEY,
        BestFRISensor::sense
    );

    // TODO: We don't need the marine here, GOAP should support this case.
    public static final Compose<Object, Option<FRIStrategyResult<? extends ItemTarget>>, ItemTarget.Location> BEST_FRI_LOCATION =
        Sensors.compose(
            BestFRISensor.KEY,
            StateKey.sensed("best_fri_location"),
            ($1, bfriOption) -> bfriOption.map(result -> result.itemTarget().location()).unwrapOr(ItemTarget.Location.NONE)
        );

    public static final Compose<LivingEntity, Option<FRIStrategyResult<ItemTarget.World>>, Boolean> IS_BEST_WORLD_FRI_IN_RANGE =
        Sensors
            .compose(
                BestFRIInWorldSensor.KEY,
                StateKey.sensed("is_best_world_fri_in_range"),
                (livingEntity, bfriOption) -> bfriOption.isSomeAnd(
                    result -> livingEntity.distanceToSqr(result.itemTarget().itemEntity()) < 4
                )
            );
}
