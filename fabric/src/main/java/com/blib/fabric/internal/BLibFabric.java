package com.blib.fabric.internal;

import net.fabricmc.api.ModInitializer;

import com.blib.api.BLibAPI;

public class BLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BLibAPI.initialize();
    }
}
