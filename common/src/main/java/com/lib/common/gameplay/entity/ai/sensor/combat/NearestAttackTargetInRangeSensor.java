package com.lib.common.gameplay.entity.ai.sensor.combat;

import com.bvanseg.just.functional.option.None;
import com.bvanseg.just.functional.option.Option;
import com.bvanseg.just.functional.option.Some;
import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import com.lib.common.gameplay.goap.state.GOAPWorldState;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiPredicate;

public class NearestAttackTargetInRangeSensor<T extends LivingEntity> implements GOAPSensor<T> {

    private final BiPredicate<GOAPWorldState, Double> distanceSqrToTargetPredicate;

    public NearestAttackTargetInRangeSensor(BiPredicate<GOAPWorldState, Double> distanceSqrToTargetPredicate) {
        this.distanceSqrToTargetPredicate = distanceSqrToTargetPredicate;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearestTargetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        switch (nearestTargetOption) {
            case None<? extends LivingEntity> target -> { /* NO-OP */ }
            case Some<? extends LivingEntity> some -> worldState.set(
                GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE,
                distanceSqrToTargetPredicate.test(worldState, context.distanceToSqr(some.unwrap()))
            );
        }
    }
}
