package com.blib.common.registry.impl;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.model.BLibModState;
import com.blib.internal.service.BLibInternalServices;

public class BLibReloadListenerRegistry {

    private final BLibMod mod;

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
