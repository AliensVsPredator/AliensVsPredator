package com.alien.common.registry;

import com.alien.common.model.lifecycle.AlienLifecycle;
import com.alien.common.model.lifecycle.growth.AlienGrowthStageKey;
import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AlienLifecycleRegistry {

    private static final List<AlienLifecycle> LIFECYCLES = new ArrayList<>();

    private static final Map<AlienGrowthStageKey, GrowthStage> ALIEN_MATURATION_LOOKUP_MAP = new HashMap<>();

    // TODO: Reduce garbage created in this function.
    public static @Nullable GrowthStage getOrNull(EntityType<?> host, EntityType<?> currentForm) {
        var directMapping = ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthStageKey(host, currentForm));

        return directMapping == null
            ? ALIEN_MATURATION_LOOKUP_MAP.get(new AlienGrowthStageKey(null, currentForm))
            : directMapping;
    }

    public static Option<GrowthStage> get(EntityType<?> host, EntityType<?> currentForm) {
        return Option.ofNullable(getOrNull(host, currentForm));
    }

    public static AlienLifecycle register(AlienLifecycle lifecycle) {
        LIFECYCLES.add(lifecycle);
        return lifecycle;
    }

    public static void rebuildLookupMappings() {
        ALIEN_MATURATION_LOOKUP_MAP.clear();
        LIFECYCLES.forEach(AlienLifecycleRegistry::compute);
    }

    private static void compute(AlienLifecycle alienLifecycle) {
        var stages = alienLifecycle.stages();

        if (alienLifecycle.hostKey() == null) {
            stages.forEach(stage -> {
                var lookupKey = new AlienGrowthStageKey(null, stage.from());
                ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, stage);
            });
        } else {
            var holderEntityTypeSetOptional = BuiltInRegistries.ENTITY_TYPE.getTag(alienLifecycle.hostKey());

            holderEntityTypeSetOptional.ifPresent(holderEntityTypeSet -> {
                holderEntityTypeSet.stream()
                    // For every host...
                    .forEach(entityTypeHolder -> {
                        var host = entityTypeHolder.unwrap().map(BuiltInRegistries.ENTITY_TYPE::get, Function.identity());

                        // For every step...
                        stages.forEach(stage -> {
                            // map the lookup key to the step. Aliens will use their host + self type combination
                            // to look up what step they are currently on.
                            var lookupKey = new AlienGrowthStageKey(host, stage.from());
                            ALIEN_MATURATION_LOOKUP_MAP.put(lookupKey, stage);
                        });
                    });
            });

        }
    }
}
