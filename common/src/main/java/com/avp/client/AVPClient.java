package com.avp.client;

import com.alien.client.AlienClient;
import com.human.client.HumanClient;

import com.avp.client.input.keybind.AVPKeybindingRegistry;

public class AVPClient {

    public static void initialize() {
        AlienClient.initialize();
        HumanClient.initialize();

        // Keybindings
        AVPKeybindingRegistry.initialize();
    }
}
