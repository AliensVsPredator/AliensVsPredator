package com.blib.api.client.input.v1;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class KeyMappingUtil {

    public static @NotNull KeyMapping createKeyMapping(ResourceLocation resourceLocation, String category, int key) {
        return new KeyMapping(
            "key." + resourceLocation.getNamespace() + "." + resourceLocation.getPath(),
            InputConstants.Type.KEYSYM,
            key,
            "keybind.category." + resourceLocation.getNamespace() + "." + category
        );
    }
}
