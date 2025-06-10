package com.human.common.gameplay.util;

import net.minecraft.util.RandomSource;

public class EyeColorGenerator {

    public static int random(RandomSource randomSource) {
        float hue;
        float saturation;
        float lightness;

        // 0=amber, 1=hazel, 2=green, 3=blue, 4=gray, 5=brown
        var eyeType = randomSource.nextInt(6);

        switch (eyeType) {
            case 0: // Amber (golden-honey)
                hue = 35 + randomSource.nextFloat() * 10f; // golden range: 35–45
                saturation = 0.5f + randomSource.nextFloat() * 0.2f;
                lightness = 0.35f + randomSource.nextFloat() * 0.15f;
                break;
            case 1: // Hazel (green-brown mix)
                hue = 60 + randomSource.nextFloat() * 30f; // yellow-green to green: 60–90
                saturation = 0.4f + randomSource.nextFloat() * 0.2f;
                lightness = 0.3f + randomSource.nextFloat() * 0.15f;
                break;
            case 2: // Green
                hue = 90 + randomSource.nextFloat() * 30f; // 90–120
                saturation = 0.45f + randomSource.nextFloat() * 0.2f;
                lightness = 0.35f + randomSource.nextFloat() * 0.15f;
                break;
            case 3: // Blue
                hue = 200 + randomSource.nextFloat() * 20f; // 200–220
                saturation = 0.5f + randomSource.nextFloat() * 0.3f;
                lightness = 0.45f + randomSource.nextFloat() * 0.2f;
                break;
            case 4: // Gray
                hue = 210 + randomSource.nextFloat() * 10f; // similar to blue, just desaturated
                saturation = 0.1f + randomSource.nextFloat() * 0.1f;
                lightness = 0.5f + randomSource.nextFloat() * 0.2f;
                break;
            case 5: // Brown
            default:
                hue = 20 + randomSource.nextFloat() * 10f; // 20–30
                saturation = 0.4f + randomSource.nextFloat() * 0.2f;
                lightness = 0.2f + randomSource.nextFloat() * 0.1f;
                break;
        }

        return ColorUtil.hslToRgbInt(hue, saturation, lightness);
    }
}
