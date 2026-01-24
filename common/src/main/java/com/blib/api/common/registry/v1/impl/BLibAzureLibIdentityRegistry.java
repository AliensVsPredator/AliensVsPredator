package com.blib.api.common.registry.v1.impl;

import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
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

        BLibInternalServices.EVENT
            .onCommonSetup(mod)
            .register(() -> AzIdentityRegistry.register(holder.get()));
    }
}
