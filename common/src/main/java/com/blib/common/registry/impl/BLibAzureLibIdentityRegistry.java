package com.blib.common.registry.impl;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.model.BLibModState;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibInternalServices;

public class BLibAzureLibIdentityRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibAzureLibIdentityRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(BLibHolder<? extends Item> holder) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register AzureLib item identity outside of mod's initialization window. Item BLibHolder: %s, Mod State: %s"
                    .formatted(holder, mod.state())
            );
        }

        BLibInternalServices.REGISTRY.registerAzureLibIdentity(holder);
    }
}
