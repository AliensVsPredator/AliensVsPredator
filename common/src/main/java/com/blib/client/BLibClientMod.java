package com.blib.client;

import java.util.Objects;

import com.blib.BLibMod;
import com.blib.client.model.access.BLibClientEventAccess;
import com.blib.client.model.access.BLibClientRegistryAccess;
import com.blib.internal.client.service.BLibInternalClientServices;

public class BLibClientMod {

    public static BLibClientMod createFor(BLibMod mod) {
        return new BLibClientMod(mod);
    }

    private final BLibMod mod;

    private final BLibClientRegistryAccess clientRegistryAccess;

    private final BLibClientEventAccess clientEventAccess;

    private BLibClientMod(BLibMod mod) {
        this.mod = mod;
        this.clientRegistryAccess = new BLibClientRegistryAccess(this);
        this.clientEventAccess = new BLibClientEventAccess(this);
    }

    public void initialize() {
        BLibInternalClientServices.CLIENT_MOD.initialize(this);
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
