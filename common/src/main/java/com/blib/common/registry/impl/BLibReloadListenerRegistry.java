package com.blib.common.registry.impl;

import com.blib.BLibMod;
import com.blib.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;
import com.blib.mod.BLibModState;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class BLibReloadListenerRegistry {

    private final BLibMod mod;

    public BLibReloadListenerRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(String path, PreparableReloadListener reloadListener) {
        if (mod.getState() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a reload listener outside of mod's initialization window. Reload Listener: %s Mod State: %s"
                    .formatted(
                        reloadListener,
                        mod.getState()
                    )
            );
        }

        BLibInternalServices.REGISTRY.registerReloadListener(mod, path, reloadListener);
    }
}
