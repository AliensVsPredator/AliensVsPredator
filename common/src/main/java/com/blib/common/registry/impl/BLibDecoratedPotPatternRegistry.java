package com.blib.common.registry.impl;

import net.minecraft.world.item.Item;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.mod.BLibModState;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibInternalServices;

public class BLibDecoratedPotPatternRegistry {

    private final BLibMod mod;

    public BLibDecoratedPotPatternRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(String path, BLibHolder<? extends Item> holder) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a BLibHolder outside of mod's initialization window. BLibHolder: %s, Mod State: %s".formatted(
                    holder,
                    mod.state()
                )
            );
        }

        BLibInternalServices.REGISTRY.registerDecoratedPotPattern(path, holder);
    }
}
