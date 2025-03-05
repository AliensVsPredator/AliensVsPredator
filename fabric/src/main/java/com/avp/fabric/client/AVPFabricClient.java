package com.avp.fabric.client;

import com.avp.core.client.AVPClient;
import com.avp.core.client.input.keybind.AVPKeybindingRegistry;
import com.avp.core.client.network.ClientPacketRegistry;
import net.fabricmc.api.ClientModInitializer;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        // Keybindings
        AVPKeybindingRegistry.initialize();

        // Networking
        ClientPacketRegistry.initialize();
    }
}
