package com.avp.common.registry.init;

import com.human.common.gameplay.entity.living.human.marine.MarineSpawning;
import com.human.common.registry.init.entity_type.HumanEntityTypes;

import com.avp.AVP;
import com.avp.common.model.spawning.AVPEntitySpawnData;
import com.avp.service.Services;

public class AVPEntitySpawns {

    public static void initialize() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(HumanEntityTypes.MARINE)
                .withSpawnPredicate(MarineSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.MARINE_SPAWN)
                // Prevents marine biome spawn configurations from being generated.
                .disableConfig()
                .build()
        );
    }

}
