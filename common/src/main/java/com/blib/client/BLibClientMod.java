package com.blib.client;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;

public class BLibClientMod {

    public static BLibClientMod createFor(BLibMod mod) {
        return new BLibClientMod(mod);
    }

    private final BLibMod mod;

    private BLibClientMod(BLibMod mod) {
        this.mod = mod;
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
}
