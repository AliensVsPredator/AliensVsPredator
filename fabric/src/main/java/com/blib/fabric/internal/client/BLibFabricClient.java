package com.blib.fabric.internal.client;

import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClient;

@ApiStatus.Internal
public class BLibFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BLibClient.initialize();
    }
}
