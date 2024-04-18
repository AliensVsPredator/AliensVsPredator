package org.avp.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.avp.api.Tuple;
import org.avp.common.network.payload.ServerboundWeaponReloadRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapAmmunitionTypeRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapFireModeRequestPayload;
import org.avp.common.service.Services;

public class AVPClientKeyBindings {

    private static final List<Tuple<KeyMapping, BiConsumer<KeyMapping, Minecraft>>> ENTRIES = new ArrayList<>();

    public static List<Tuple<KeyMapping, BiConsumer<KeyMapping, Minecraft>>> getEntries() {
        return ENTRIES;
    }

    private static void registerKeyBinding(String name, String category, int key, BiConsumer<KeyMapping, Minecraft> biConsumer) {
        var keyMapping = new KeyMapping(
            "key.avp." + name,
            InputConstants.Type.KEYSYM,
            key,
            "keybind.category.avp." + category
        );

        ENTRIES.add(new Tuple<>(keyMapping, biConsumer));
    }

    static {
        registerKeyBinding(
            "reload",
            "weapons",
            GLFW.GLFW_KEY_R,
            (keyBinding, client) -> {
                var player = client.player;
                if (player != null) {
                    var payload = new ServerboundWeaponReloadRequestPayload(player.getUUID());
                    Services.NETWORK_SERVICE.sendToServer(payload);
                }
            }
        );
        registerKeyBinding(
            "swap_fire_mode",
            "weapons",
            GLFW.GLFW_KEY_Z,
            (keyBinding, client) -> {
                var player = client.player;
                if (player != null) {
                    var payload = new ServerboundWeaponSwapFireModeRequestPayload(player.getUUID());
                    Services.NETWORK_SERVICE.sendToServer(payload);
                }
            }
        );
        registerKeyBinding(
            "swap_ammunition_type",
            "weapons",
            GLFW.GLFW_KEY_X,
            (keyBinding, client) -> {
                var player = client.player;
                if (player != null) {
                    var payload = new ServerboundWeaponSwapAmmunitionTypeRequestPayload(player.getUUID());
                    Services.NETWORK_SERVICE.sendToServer(payload);
                }
            }
        );
    }

    private AVPClientKeyBindings() {
        throw new UnsupportedOperationException();
    }
}
