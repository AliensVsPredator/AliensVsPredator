package com.human.common.gameplay.util;

public class ColorUtil {

    public static int hslToRgbInt(float h, float s, float l) {
        h = h / 360f;

        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1f + s) : (l + s - l * s);
            float p = 2f * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }

        var red = Math.round(r * 255);
        var green = Math.round(g * 255);
        var blue = Math.round(b * 255);

        return (0xFF << 24) | (red << 16) | (green << 8) | blue; // ARGB
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f)
            t += 1f;
        if (t > 1f)
            t -= 1f;
        if (t < 1f / 6f)
            return p + (q - p) * 6f * t;
        if (t < 1f / 2f)
            return q;
        if (t < 2f / 3f)
            return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }
}
