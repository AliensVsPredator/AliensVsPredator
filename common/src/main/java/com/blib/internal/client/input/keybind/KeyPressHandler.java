package com.blib.internal.client.input.keybind;

import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.function.Consumer;

import com.blib.api.client.input.v1.model.KeyInteractType;

@ApiStatus.Internal
public class KeyPressHandler {

    private static final HashSet<KeyMapping> PRESSED_KEYS = new HashSet<>();

    public static void handle(KeyMapping keyMapping, Consumer<KeyInteractType> keyInteractTypeConsumer) {
        if (!PRESSED_KEYS.contains(keyMapping)) {
            if (keyMapping.isDown()) {
                PRESSED_KEYS.add(keyMapping);
                keyInteractTypeConsumer.accept(KeyInteractType.PRESS);
                return;
            }
        }

        if (!keyMapping.isDown() && PRESSED_KEYS.contains(keyMapping)) {
            keyInteractTypeConsumer.accept(KeyInteractType.RELEASE);
            PRESSED_KEYS.remove(keyMapping);
        }
    }
}
