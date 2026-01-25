package com.blib.api.common.registry.v1.impl;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.common.BLibDecoratedPotPatternCache;
import com.blib.internal.service.BLibInternalServices;

public class BLibDecoratedPotPatternRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
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

        BLibInternalServices.EVENT
            .onCommonSetup(mod)
            .register(
                () -> BLibDecoratedPotPatternCache.put(
                    holder.get(),
                    mod.resources().createKey(Registries.DECORATED_POT_PATTERN, path)
                )
            );
    }
}
