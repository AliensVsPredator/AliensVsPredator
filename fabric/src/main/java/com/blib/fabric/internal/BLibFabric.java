package com.blib.fabric.internal;

import net.fabricmc.api.ModInitializer;

import com.blib.azurelib.fabric.FabricAzureLibMod;
import com.blib.mod.BLib;

public class BLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricAzureLibMod.onInitialize();
        BLib.initialize();
    }
}
