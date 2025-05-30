package com.lib.common.gameplay.entity.ai.sensor.combat;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.function.Predicate;

public class NearbyAttackTargetEntitiesSensor<T extends Mob> implements GOAPSensor<T> {

    private final Predicate<LivingEntity> nearbyTargetPredicate;

    public NearbyAttackTargetEntitiesSensor(Predicate<LivingEntity> nearbyTargetPredicate) {
        this.nearbyTargetPredicate = nearbyTargetPredicate;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearbyLivingEntities = worldState.getOrDefault(GOAPConstants.NEARBY_LIVING_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEARBY_ATTACK_TARGET_ENTITIES,
            nearbyLivingEntities.stream()
                .filter(
                    livingEntity -> livingEntity.isAlive()
                        && nearbyTargetPredicate.test(livingEntity)
                        && context.getSensing().hasLineOfSight(livingEntity)
                )
                .toList()
        );
    }
}
