package com.avp.client.input.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import com.avp.AVP;
import com.avp.client.network.AVPClientNetworking;
import com.avp.common.network.packet.C2SGunReloadPayload;

public class AVPKeybindingRegistry {

    public static final KeyMapping RELOAD = register("reload", "weapons", GLFW.GLFW_KEY_R, keyMapping -> {
        var player = Minecraft.getInstance().player;

        if (player != null) {
            AVPClientNetworking.sendToServer(C2SGunReloadPayload.INSTANCE);
        }
    });

    private static KeyMapping register(String name, String category, int key, Consumer<KeyMapping> consumer) {
        var keyMapping = new KeyMapping(
            "key." + AVP.MOD_ID + "." + name,
            InputConstants.Type.KEYSYM,
            key,
            "keybind.category." + AVP.MOD_ID + "." + category
        );

        KeyBindingHelper.registerKeyBinding(keyMapping);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyMapping.consumeClick()) {
                consumer.accept(keyMapping);
            }
        });

        return keyMapping;
    }

    public static void initialize() {}
}
