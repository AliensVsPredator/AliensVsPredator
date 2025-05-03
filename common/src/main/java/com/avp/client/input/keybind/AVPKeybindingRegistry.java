package com.avp.client.input.keybind;

import com.bvanseg.just.functional.tuple.Tuple2;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.service.Services;

public class AVPKeybindingRegistry {

    public static final Supplier<Tuple2<KeyMapping, Runnable>> RELOAD = register("reload", "weapons", GLFW.GLFW_KEY_R, () -> {
        var player = Minecraft.getInstance().player;

        if (player != null) {
            Services.CLIENT_NETWORKING.sendToServer(C2SGunReloadPayload.INSTANCE);
        }
    });

    private static Supplier<Tuple2<KeyMapping, Runnable>> register(String id, String category, int key, Runnable onKeyMappingActivated) {
        return Services.CLIENT_REGISTRY.registerKeyMapping(id, category, key, onKeyMappingActivated);
    }

    public static void initialize() {}
}
