package com.blib.api.common.registry.v1.impl;

import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibCompostableRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
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
                "Attempted to register a compostable outside of mod's initialization window. ItemLike BLibHolder: %s, Mod State: %s"
                    .formatted(
                        holder,
                        mod.state()
                    )
            );
        }

        BLibInternalServices.REGISTRY.registerCompostable(holder, chance, villagersCanCompost, replace);
    }
}
