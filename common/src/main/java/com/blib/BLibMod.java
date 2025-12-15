package com.blib;

import com.blib.common.exception.BLibModInitializationException;
import com.blib.common.mod.BLibModState;
import com.blib.common.model.Version;
import com.blib.common.registry.BLibRegistryAccess;
import com.blib.common.registry.BLibResourceAccess;
import com.blib.internal.service.BLibInternalServices;
import org.jetbrains.annotations.Nullable;

public class BLibMod {

    private final String id;

    private final BLibRegistryAccess registryAccess;

    private final BLibResourceAccess resourceAccess;

    private final @Nullable Version version;

    private volatile BLibModState state;

    /* package-private */ BLibMod(String id) {
        this.id = id;
        this.registryAccess = new BLibRegistryAccess(this);
        this.resourceAccess = new BLibResourceAccess(this);
        this.version = BLibInternalServices.MOD_LOADER.getModVersion(id);
        this.state = BLibModState.UNINITIALIZED;
    }

    public void initialize(Runnable runnable) {
        if (state != BLibModState.UNINITIALIZED) {
            throw new BLibModInitializationException(
                "Attempted to initialize a mod that is either initializing or already initialized. Mod State: %s".formatted(state)
            );
        }

        this.state = BLibModState.INITIALIZING;
        runnable.run();
        BLibInternalServices.MOD.postInitialize(this);
        this.state = BLibModState.INITIALIZED;
    }

    public boolean isLoaded() {
        return BLib.isModLoaded(id);
    }

    public String id() {
        return id;
    }

    public BLibRegistryAccess registries() {
        return registryAccess;
    }

    public BLibResourceAccess resources() {
        return resourceAccess;
    }

    public BLibModState state() {
        return state;
    }

    public @Nullable Version version() {
        return version;
    }
}
