package com.blib.common.registry.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;
import com.blib.mod.BLibModState;
import net.minecraft.world.level.ItemLike;

public class BLibFurnaceFuelRegistry {

    private final BLibMod mod;

    public BLibFurnaceFuelRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks) {
        if (mod.getState() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a BLibHolder outside of mod's initialization window. BLibHolder: %s Mod State: %s".formatted(
                    holder,
                    mod.getState()
                )
            );
        }

        BLibInternalServices.REGISTRY.registerFurnaceFuel(holder, burnTimeInTicks);
    }
}
