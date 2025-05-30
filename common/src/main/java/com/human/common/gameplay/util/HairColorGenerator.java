package com.human.common.gameplay.util;

import net.minecraft.util.RandomSource;

public class HairColorGenerator {

    public static int random(RandomSource randomSource) {
        float hue;
        float saturation;
        float lightness;

        // 0=blonde, 1=red, 2=light brown, 3=dark brown, 4=black
        var hairType = randomSource.nextInt(5);

        switch (hairType) {
            case 0: // Blonde
                hue = 40 + randomSource.nextFloat() * 10f; // golden yellow (40–50)
                saturation = 0.4f + randomSource.nextFloat() * 0.2f; // 0.4–0.6
                lightness = 0.7f + randomSource.nextFloat() * 0.15f; // 0.7–0.85
                break;
            case 1: // Red
                hue = 5 + randomSource.nextFloat() * 15f; // reddish-orange (5–20)
                saturation = 0.6f + randomSource.nextFloat() * 0.3f; // 0.6–0.9
                lightness = 0.45f + randomSource.nextFloat() * 0.2f; // 0.45–0.65
                break;
            case 2: // Light brown
                hue = 25 + randomSource.nextFloat() * 10f; // light orange-brown (25–35)
                saturation = 0.45f + randomSource.nextFloat() * 0.2f;
                lightness = 0.5f + randomSource.nextFloat() * 0.15f;
                break;
            case 3: // Dark brown
                hue = 20 + randomSource.nextFloat() * 10f; // deep brown (20–30)
                saturation = 0.4f + randomSource.nextFloat() * 0.2f;
                lightness = 0.25f + randomSource.nextFloat() * 0.1f;
                break;
            case 4: // Black
            default:
                hue = 20 + randomSource.nextFloat() * 10f; // not much hue in black
                saturation = 0.2f + randomSource.nextFloat() * 0.1f;
                lightness = 0.08f + randomSource.nextFloat() * 0.07f; // very dark
                break;
        }

        return ColorUtil.hslToRgbInt(hue, saturation, lightness);
    }
}
