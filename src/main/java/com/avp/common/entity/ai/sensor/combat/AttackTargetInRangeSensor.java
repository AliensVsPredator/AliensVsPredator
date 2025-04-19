package com.avp.common.entity.ai.sensor.combat;

import com.bvanseg.just.functional.option.None;
import com.bvanseg.just.functional.option.Option;
import com.bvanseg.just.functional.option.Some;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiPredicate;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class AttackTargetInRangeSensor<T extends LivingEntity> implements GOAPSensor<T> {

    private final BiPredicate<T, Double> distanceSqrToTargetPredicate;

    public AttackTargetInRangeSensor(BiPredicate<T, Double> distanceSqrToTargetPredicate) {
        this.distanceSqrToTargetPredicate = distanceSqrToTargetPredicate;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearestTargetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        switch (nearestTargetOption) {
            case None<? extends LivingEntity> target -> { /* NO-OP */ }
            case Some<? extends LivingEntity> some -> worldState.set(
                GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE,
                distanceSqrToTargetPredicate.test(context, context.distanceToSqr(some.unwrap()))
            );
        }
    }
}
