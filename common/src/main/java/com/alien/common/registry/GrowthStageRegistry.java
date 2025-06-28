package com.alien.common.registry;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import com.alien.common.model.lifecycle.growth.GrowthStageKey;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrowthStageRegistry {

    private static final List<GrowthStage> GROWTH_STAGES = new ArrayList<>();

    private static final Map<GrowthStageKey, GrowthStage> GROWTH_STAGE_KEY_TO_GROWTH_STAGE = new HashMap<>();

    public static @Nullable GrowthStage getOrNull(EntityType<?> host, EntityType<?> currentForm) {
        var directMapping = GROWTH_STAGE_KEY_TO_GROWTH_STAGE.get(new GrowthStageKey(host, currentForm));

        return directMapping == null
            ? GROWTH_STAGE_KEY_TO_GROWTH_STAGE.get(new GrowthStageKey(null, currentForm))
            : directMapping;
    }

    public static void clear() {
        GROWTH_STAGES.clear();
    }

    public static void register(GrowthStage growthStage) {
        GROWTH_STAGES.add(growthStage);
    }

    public static void rebuildLookupMappings() {
        GROWTH_STAGE_KEY_TO_GROWTH_STAGE.clear();
        GROWTH_STAGES.forEach(GrowthStageRegistry::compute);
    }

    public static Option<GrowthStage> get(EntityType<?> host, EntityType<?> currentForm) {
        return Option.ofNullable(getOrNull(host, currentForm));
    }

    private static void compute(GrowthStage growthStage) {
        var hostTypePredicate = growthStage.hostTypePredicate().orElse(null);

        if (hostTypePredicate == null) {
            var lookupKey = new GrowthStageKey(null, growthStage.from());
            GROWTH_STAGE_KEY_TO_GROWTH_STAGE.put(lookupKey, growthStage);
        } else {
            BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(hostTypePredicate::test)
                .forEach(entityType -> {
                    // map the lookup key to the step. Aliens will use their host + self type combination
                    // to look up what step they are currently on.
                    var lookupKey = new GrowthStageKey(entityType, growthStage.from());
                    GROWTH_STAGE_KEY_TO_GROWTH_STAGE.put(lookupKey, growthStage);
                });
        }
    }

}
