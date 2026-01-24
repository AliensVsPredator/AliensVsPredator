package com.blib.api.common.registry.v1.impl;

import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibFurnaceFuelRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibFurnaceFuelRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a furnace fuel outside of mod's initialization window. ItemLike BLibHolder: %s, Mod State: %s"
                    .formatted(
                        holder,
                        mod.state()
                    )
            );
        }

        BLibInternalServices.REGISTRY.registerFurnaceFuel(holder, burnTimeInTicks);
    }
}
