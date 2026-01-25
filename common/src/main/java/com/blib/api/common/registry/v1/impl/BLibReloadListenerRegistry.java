package com.blib.api.common.registry.v1.impl;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibReloadListenerRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibReloadListenerRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(String path, PreparableReloadListener reloadListener) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a reload listener outside of mod's initialization window. Reload Listener: %s, Mod State: %s"
                    .formatted(
                        reloadListener,
                        mod.state()
                    )
            );
        }

        BLibInternalServices.REGISTRY.registerReloadListener(mod, path, reloadListener);
    }
}
