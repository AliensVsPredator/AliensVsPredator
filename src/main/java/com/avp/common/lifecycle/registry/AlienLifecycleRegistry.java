package com.avp.common.lifecycle.registry;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.growth.AlienGrowthStageKey;
import com.avp.common.lifecycle.growth.GrowthStage;

public class AlienLifecycleRegistry {

    private static final Map<AlienGrowthStageKey, GrowthStage> ALIEN_MATURATION_LOOKUP_MAP = new HashMap<>();

    // TODO: Reduce garbage created in this function.
    public static @Nullable GrowthStage getOrNull(EntityType<?> host, EntityType<?> currentForm) {
        var directMapping = ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthStageKey(host, currentForm));

        return directMapping == null
            ? ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthStageKey(null, currentForm))
            : directMapping;
    }

    public static Optional<GrowthStage> get(EntityType<?> host, EntityType<?> currentForm) {
        return Optional.ofNullable(getOrNull(host, currentForm));
    }

    public static AlienLifecycle register(AlienLifecycle lifecycle) {
        var hosts = lifecycle.hosts();
        var stages = lifecycle.stages();

        if (hosts == null) {
            stages.forEach(stage -> {
                var lookupKey = new AlienGrowthStageKey(null, stage.from());
                ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, stage);
            });
        } else {
            // For every host, for every step, map the lookup key to the step.
            // Aliens will use their host + self type combination to look up what step they are currently on.
            hosts.forEach(host -> stages.forEach(stage -> {
                var lookupKey = new AlienGrowthStageKey(host, stage.from());
                ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, stage);
            }));
        }

        return lifecycle;
    }
}
