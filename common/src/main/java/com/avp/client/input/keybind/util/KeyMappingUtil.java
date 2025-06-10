package com.avp.client.input.keybind.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;

public class KeyMappingUtil {

    public static @NotNull KeyMapping createKeyMapping(String id, String category, int key) {
        return new KeyMapping(
            "key." + AVP.MOD_ID + "." + id,
            InputConstants.Type.KEYSYM,
            key,
            "keybind.category." + AVP.MOD_ID + "." + category
        );
    }
}
