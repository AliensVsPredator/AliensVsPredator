package com.avp.common.entity.ai.sensor.combat;

import com.bvanseg.just.functional.option.Option;
import com.xlib.goap.GOAPSensor;
import com.xlib.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.List;

import com.avp.common.entity.ai.GOAPConstants;

public class NearestAttackTargetEntitySensor implements GOAPSensor<LivingEntity> {

    public static final NearestAttackTargetEntitySensor INSTANCE = new NearestAttackTargetEntitySensor();

    private NearestAttackTargetEntitySensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        var nearbyTargets = worldState.getOrDefault(GOAPConstants.NEARBY_ATTACK_TARGET_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY,
            Option.ofNullable(
                nearbyTargets.stream()
                    .min(Comparator.comparingDouble((LivingEntity target) -> target.distanceTo(context)))
                    .orElse(null)
            )
        );
    }
}
