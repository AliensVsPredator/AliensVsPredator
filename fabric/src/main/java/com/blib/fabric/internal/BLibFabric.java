package com.blib.fabric.internal;

import net.fabricmc.api.ModInitializer;

import com.blib.mod.BLib;

public class BLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BLib.initialize();
    }
}
