package com.alien.common.gameplay.ai;

import com.just.goap.StateKey;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class AlienGOAPKeys {

    public static final StateKey.Sensed<Boolean> HAS_NEARBY_HOST = StateKey.sensed("has_nearby_host");

    public static final StateKey.Sensed<List<LivingEntity>> NEARBY_HOSTS = StateKey.sensed("nearby_hosts");

    private AlienGOAPKeys() {
        throw new UnsupportedOperationException();
    }
}
