package com.avp.fabric.client;

import net.fabricmc.api.ClientModInitializer;

import com.avp.client.AVPClient;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();
    }
}
