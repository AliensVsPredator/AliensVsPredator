package com.avp.common.entity.ai.sensor.combat;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.BiPredicate;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.Mob;

public class NearbyAttackTargetEntitiesSensor<T extends Mob> implements GOAPSensor<T> {

    private final BiPredicate<T, LivingEntity> nearbyTargetPredicate;

    public NearbyAttackTargetEntitiesSensor(BiPredicate<T, LivingEntity> nearbyTargetPredicate) {
        this.nearbyTargetPredicate = nearbyTargetPredicate;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearbyLivingEntities = worldState.getOrDefault(GOAPConstants.NEARBY_LIVING_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEARBY_ATTACK_TARGET_ENTITIES,
            nearbyLivingEntities.stream()
                .filter(livingEntity -> livingEntity.isAlive()
                    && nearbyTargetPredicate.test(context, livingEntity)
                    && context.getSensing().hasLineOfSight(livingEntity)
                )
                .toList()
        );
    }
}
