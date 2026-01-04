package com.blib.internal.client;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.client.BLibClientMod;

public final class BLibClient {

    @ApiStatus.Internal
    public static final BLibClientMod MOD = BLibClientMod.createFor(BLib.MOD);

    @ApiStatus.Internal
    public static void initialize() {
        MOD.initialize();
    }

    @ApiStatus.Internal
    private BLibClient() {
        throw new UnsupportedOperationException();
    }
}
