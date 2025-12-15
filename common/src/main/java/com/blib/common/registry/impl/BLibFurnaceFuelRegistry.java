package com.blib.common.registry.impl;

import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.model.BLibModState;
import com.blib.common.registry.BLibHolder;
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
                "Attempted to register a BLibHolder outside of mod's initialization window. BLibHolder: %s, Mod State: %s".formatted(
                    holder,
                    mod.state()
                )
            );
        }

        BLibInternalServices.REGISTRY.registerFurnaceFuel(holder, burnTimeInTicks);
    }
}
