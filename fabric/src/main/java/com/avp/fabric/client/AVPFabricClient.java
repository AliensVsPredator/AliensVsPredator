package com.avp.fabric.client;

import net.fabricmc.api.ClientModInitializer;

import com.avp.client.AVPClient;
import com.avp.fabric.client.input.keybind.AVPKeybindingRegistry;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        // Keybindings
        AVPKeybindingRegistry.initialize();
    }
}
