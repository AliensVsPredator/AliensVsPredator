package com.alien.common.gameplay.ai;

import com.just.goap.GOAPKey;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class AlienGOAPKeys {

    public static final GOAPKey<Boolean> HAS_NEARBY_HOST = new GOAPKey<>("has_nearby_host");

    public static final GOAPKey<List<LivingEntity>> NEARBY_HOSTS = new GOAPKey<>("nearby_hosts");

    private AlienGOAPKeys() {
        throw new UnsupportedOperationException();
    }
}
