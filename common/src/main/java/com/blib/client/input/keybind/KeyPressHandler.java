package com.blib.client.input.keybind;

import com.blib.client.model.KeyInteractType;
import net.minecraft.client.KeyMapping;

import java.util.HashSet;
import java.util.function.Consumer;

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
