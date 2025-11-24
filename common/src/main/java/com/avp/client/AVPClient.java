package com.avp.client;

import com.avp.client.input.keybind.AVPKeybindingRegistry;
import com.human.client.HumanClient;

public class AVPClient {

    public static void initialize() {
        HumanClient.initialize();

        // Keybindings
        AVPKeybindingRegistry.initialize();
    }
}
