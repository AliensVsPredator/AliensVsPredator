package com.blib.api.common.registry.v1.impl;

import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.entity.v1.spawning.BLibEntitySpawnData;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibEntitySpawnRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibEntitySpawnRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public <T extends Mob> void register(BLibEntitySpawnData<T> spawnData) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register entity spawn data outside of mod's initialization window. BLibEntitySpawnData: %s, Mod State: %s"
                    .formatted(
                        spawnData,
                        mod.state()
                    )
            );
        }

        BLibInternalServices.REGISTRY.registerEntitySpawnData(spawnData);
    }
}
