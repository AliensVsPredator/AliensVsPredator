package com.blib.common.registry.impl;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.mod.BLibModState;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibInternalServices;
import net.minecraft.world.level.ItemLike;

public class BLibCompostableRegistry {

    private final BLibMod mod;

    public BLibCompostableRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(
        BLibHolder<? extends ItemLike> holder,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a BLibHolder outside of mod's initialization window. BLibHolder: %s, Mod State: %s".formatted(
                    holder,
                    mod.state()
                )
            );
        }

        BLibInternalServices.REGISTRY.registerCompostable(holder, chance, villagersCanCompost, replace);
    }
}
