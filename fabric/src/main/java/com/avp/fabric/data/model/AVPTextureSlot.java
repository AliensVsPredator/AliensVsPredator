package com.avp.fabric.data.model;

import net.minecraft.data.models.model.TextureSlot;

public class AVPTextureSlot {

    public static final TextureSlot BARS = create("bars", TextureSlot.ALL);

    public static TextureSlot create(String string, TextureSlot textureSlot) {
        return TextureSlot.create(string, textureSlot);
    }
}
