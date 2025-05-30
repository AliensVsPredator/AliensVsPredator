package com.human.common.gameplay.util;

import net.minecraft.util.RandomSource;

public class SkinColorGenerator {

    public static int random(RandomSource randomSource) {
        // Hue: 10–30 (reddish)
        var hue = 10 + randomSource.nextFloat() * 20f;
        // Saturation: 0.3–0.55
        var saturation = 0.3f + randomSource.nextFloat() * 0.25f;
        // Lightness: 0.4–0.75
        var lightness = 0.4f + randomSource.nextFloat() * 0.35f;

        return ColorUtil.hslToRgbInt(hue, saturation, lightness);
    }
}
