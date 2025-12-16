package com.blib.client;

import java.util.Objects;

import com.blib.BLibMod;
import com.blib.client.model.access.BLibClientRegistryAccess;
import com.blib.internal.service.BLibInternalServices;

public class BLibClientMod {

    public static BLibClientMod createFor(BLibMod mod) {
        return new BLibClientMod(mod);
    }

    private final BLibMod mod;

    private final BLibClientRegistryAccess clientRegistryAccess;

    private BLibClientMod(BLibMod mod) {
        this.mod = mod;
        this.clientRegistryAccess = new BLibClientRegistryAccess(this);
    }

    public void initialize() {
        BLibInternalServices.CLIENT_MOD.initialize(this);
    }

    public BLibMod common() {
        return mod;
    }

    public String id() {
        return mod.id();
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
