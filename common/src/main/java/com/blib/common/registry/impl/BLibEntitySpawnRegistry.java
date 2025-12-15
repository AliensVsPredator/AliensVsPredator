package com.blib.common.registry.impl;

import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.common.model.BLibModState;
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
