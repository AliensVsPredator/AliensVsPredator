package com.blib.client;

import com.blib.BLib;

public class BLibClient {

    public static final BLibClientMod MOD = BLibClientMod.createFor(BLib.MOD);

    public static void initialize() {
        MOD.initialize();
    }
}
