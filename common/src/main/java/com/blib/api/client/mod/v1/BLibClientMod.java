package com.blib.api.client.mod.v1;

import java.util.Objects;

import com.blib.api.client.event.v1.model.access.BLibClientEventAccess;
import com.blib.api.client.registry.v1.model.access.BLibClientRegistryAccess;
import com.blib.api.client.shader.v1.model.access.BLibClientPostEffectAccess;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.exception.BLibModInitializationException;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;
import com.blib.internal.client.service.BLibInternalClientServices;

public class BLibClientMod implements BLibModStateAccess {

    public static BLibClientMod createFor(BLibMod mod) {
        return new BLibClientMod(mod);
    }

    private final BLibMod mod;

    private final BLibClientRegistryAccess clientRegistryAccess;

    private final BLibClientEventAccess clientEventAccess;

    private final BLibClientPostEffectAccess clientPostEffectAccess;

    private volatile BLibModState state;

    private BLibClientMod(BLibMod mod) {
        this.mod = mod;
        this.clientRegistryAccess = new BLibClientRegistryAccess(this);
        this.clientEventAccess = new BLibClientEventAccess(this);
        this.clientPostEffectAccess = new BLibClientPostEffectAccess(this);
        this.state = BLibModState.UNINITIALIZED;
    }

    @Override
    public BLibModState state() {
        return state;
    }

    public void initialize() {
        initialize(() -> {});
    }

    public void initialize(Runnable runnable) {
        if (state != BLibModState.UNINITIALIZED) {
            throw new BLibModInitializationException(
                "Attempted to initialize a mod that is either initializing or already initialized. Mod State: %s".formatted(state)
            );
        }

        BLibInternalClientServices.CLIENT_MOD.initialize(this, () -> {
            this.state = BLibModState.INITIALIZING;
            runnable.run();
            this.state = BLibModState.INITIALIZED;
        });
    }

    public BLibMod common() {
        return mod;
    }

    public BLibClientEventAccess events() {
        return clientEventAccess;
    }

    public String id() {
        return mod.id();
    }

    public BLibClientPostEffectAccess postEffects() {
        return clientPostEffectAccess;
    }

    public BLibClientRegistryAccess registries() {
        return clientRegistryAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BLibClientMod mod)) {
            return false;
        }

        return Objects.equals(id(), mod.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
