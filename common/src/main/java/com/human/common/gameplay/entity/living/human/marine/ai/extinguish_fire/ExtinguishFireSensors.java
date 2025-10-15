package com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.extinguish_fire.sensor.WaterBucketInInventorySensor;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

import com.avp.common.model.inventory.AVPInventory;

public class ExtinguishFireSensors {

    public static final Sensor.Mono<Marine, Option<AVPInventory.Entry>> WATER_BUCKET_IN_INVENTORY =
        Sensors.lazyCompose(
            WaterBucketInInventorySensor.KEY,
            WaterBucketInInventorySensor::sense
        );

    public static final Sensor.Mono<LivingEntity, Boolean> HAS_WATER_BUCKET_EQUIPPED = Sensors.map(
        StateKey.sensed("has_water_bucket_equipped"),
        livingEntity -> livingEntity.getMainHandItem().is(Items.WATER_BUCKET)
    );
}
