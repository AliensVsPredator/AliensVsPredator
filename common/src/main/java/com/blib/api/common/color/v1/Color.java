package com.blib.api.common.color.v1;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record Color(int argbInt) {

    public static Codec<Color> RGBA_CODEC = RecordCodecBuilder.create((instance) -> { // float?
        return instance.group(
            Codec.INT.fieldOf("r").forGetter(Color::getRed),
            Codec.INT.fieldOf("g").forGetter(Color::getGreen),
            Codec.INT.fieldOf("b").forGetter(Color::getBlue),
            Codec.INT.fieldOf("a").orElse(255).forGetter(Color::getAlpha)
        ).apply(instance, Color::ofRGBA);
    });

    public static Codec<Color> STRING_CODEC = Codec.STRING.comapFlatMap(
        Color::tryHexString,
        Color::toString
    );

    public static final Codec<Color> INT_CODEC = Codec.INT.xmap(
        Color::new,
        color -> color.argbInt
    );

    public static final Codec<Color> CODEC = Codec.either(STRING_CODEC, RGBA_CODEC)
        .comapFlatMap(
            either -> either.map(DataResult::success, DataResult::success),
            Either::left
        );

    public static final Color WHITE = new Color(0xFFFFFFFF);

    public static final Color LIGHT_GRAY = new Color(0xFFC0C0C0);

    public static final Color GRAY = new Color(0xFF808080);

    public static final Color DARK_GRAY = new Color(0xFF404040);

    public static final Color BLACK = new Color(0xFF000000);

    public static final Color RED = new Color(0xFFFF0000);

    public static final Color PINK = new Color(0xFFFFAFAF);

    public static final Color ORANGE = new Color(0xFFFFC800);

    public static final Color YELLOW = new Color(0xFFFFFF00);

    public static final Color GREEN = new Color(0xFF00FF00);

    public static final Color MAGENTA = new Color(0xFFFF00FF);

    public static final Color CYAN = new Color(0xFF00FFFF);

    public static final Color BLUE = new Color(0xFF0000FF);

    public static Color ofOpaque(int color) {
        return new Color(0xFF000000 | color);
    }

    public static Color ofRGB(float red, float green, float blue) {
        return ofRGBA(red, green, blue, 1f);
    }

    public static Color ofRGB(int r, int g, int b) {
        return ofRGBA(r, g, b, 255);
    }

    public static Color ofRGBA(float r, float g, float b, float a) {
        return ofRGBA(
            (int) (r * 255f + 0.5),
            (int) (g * 255f + 0.5f),
            (int) (b * 255f + 0.5f),
            (int) (a * 255f + 0.5f)
        );
    }

    public static Color ofRGBA(int r, int g, int b, int a) {
        return new Color(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
    }

    public static Color ofHSB(float hue, float saturation, float brightness) {
        return ofOpaque(HSBtoARGB(hue, saturation, brightness));
    }

    public static int HSBtoARGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;

        if (saturation == 0) {
            r = g = b = (int) (brightness * 255f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1 - saturation);
            float q = brightness * (1 - saturation * f);
            float t = brightness * (1 - (saturation * (1 - f)));

            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255f + 0.5f);
                    g = (int) (t * 255f + 0.5f);
                    b = (int) (p * 255f + 0.5f);
                }
                case 1 -> {
                    r = (int) (q * 255f + 0.5f);
                    g = (int) (brightness * 255f + 0.5f);
                    b = (int) (p * 255f + 0.5f);
                }
                case 2 -> {
                    r = (int) (p * 255f + 0.5f);
                    g = (int) (brightness * 255f + 0.5f);
                    b = (int) (t * 255f + 0.5f);
                }
                case 3 -> {
                    r = (int) (p * 255f + 0.5f);
                    g = (int) (q * 255f + 0.5f);
                    b = (int) (brightness * 255f + 0.5f);
                }
                case 4 -> {
                    r = (int) (t * 255f + 0.5f);
                    g = (int) (p * 255f + 0.5f);
                    b = (int) (brightness * 255f + 0.5f);
                }
                case 5 -> {
                    r = (int) (brightness * 255f + 0.5f);
                    g = (int) (p * 255f + 0.5f);
                    b = (int) (q * 255f + 0.5f);
                }
            }
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static Color ofHSL(float hue, float saturation, float lightness) {
        return ofOpaque(HSLtoARGB(hue, saturation, lightness));
    }

    /**
     * HSL ↔ RGB. Distinct from HSB: lightness {@code 0 = black, 0.5 = full chroma, 1 = white}, whereas HSB brightness
     * has {@code 1 = full chroma at S=1, 1 = white at S=0}. Standard formula from CSS-Color-3 / Wikipedia.
     */
    public static int HSLtoARGB(float hue, float saturation, float lightness) {
        var h = hue - (float) Math.floor(hue);
        var s = Math.max(0f, Math.min(1f, saturation));
        var l = Math.max(0f, Math.min(1f, lightness));

        if (s == 0f) {
            var v = (int) (l * 255f + 0.5f);
            return 0xFF000000 | (v << 16) | (v << 8) | v;
        }

        var q = l < 0.5f ? l * (1f + s) : l + s - l * s;
        var p = 2f * l - q;
        var r = hueToRgb(p, q, h + 1f / 3f);
        var g = hueToRgb(p, q, h);
        var b = hueToRgb(p, q, h - 1f / 3f);

        var ri = (int) (r * 255f + 0.5f);
        var gi = (int) (g * 255f + 0.5f);
        var bi = (int) (b * 255f + 0.5f);
        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f) {
            t += 1f;
        }
        if (t > 1f) {
            t -= 1f;
        }
        if (t < 1f / 6f) {
            return p + (q - p) * 6f * t;
        }
        if (t < 1f / 2f) {
            return q;
        }
        if (t < 2f / 3f) {
            return p + (q - p) * (2f / 3f - t) * 6f;
        }
        return p;
    }

    /** Returns {h, s, l} each in [0, 1]. Ignores any alpha in {@code argb}. */
    public static float[] ARGBtoHSL(int argb) {
        var r = ((argb >> 16) & 0xFF) / 255f;
        var g = ((argb >> 8) & 0xFF) / 255f;
        var b = (argb & 0xFF) / 255f;

        var max = Math.max(r, Math.max(g, b));
        var min = Math.min(r, Math.min(g, b));
        var l = (max + min) / 2f;

        float h;
        float s;
        if (max == min) {
            h = 0f;
            s = 0f;
        } else {
            var d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6f : 0f);
            } else if (max == g) {
                h = (b - r) / d + 2f;
            } else {
                h = (r - g) / d + 4f;
            }
            h /= 6f;
        }
        return new float[] { h, s, l };
    }

    public static Color ofHexString(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        if (hexColor.length() == 3) {
            StringBuilder expanded = new StringBuilder();
            for (char c : hexColor.toCharArray()) {
                expanded.append(c).append(c);
            }
            hexColor = expanded.toString();
        }
        return new Color(Integer.parseInt(hexColor, 16));
    }

    public static DataResult<Color> tryHexString(String hexColor) {
        try {
            return DataResult.success(ofHexString(hexColor));
        } catch (Exception err) {
            return DataResult.error(err::toString);
        }
    }

    public int getColor() {
        return this.argbInt;
    }

    public int getAlpha() {
        return this.argbInt >> 24 & 0xFF;
    }

    public float getAlphaFloat() {
        return getAlpha() / 255f;
    }

    public int getRed() {
        return this.argbInt >> 16 & 0xFF;
    }

    public float getRedFloat() {
        return getRed() / 255f;
    }

    public int getGreen() {
        return this.argbInt >> 8 & 0xFF;
    }

    public float getGreenFloat() {
        return getGreen() / 255f;
    }

    public int getBlue() {
        return this.argbInt & 0xFF;
    }

    public float getBlueFloat() {
        return getBlue() / 255f;
    }

    public List<Integer> getList() {
        return List.of(getRed(), getGreen(), getBlue(), getAlpha());
    }

    public Color brighter(double factor) {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();
        int i = (int) (1 / (1 - (1 / factor)));

        if (r == 0 && g == 0 && b == 0)
            return ofRGBA(i, i, i, getAlpha());

        if (r > 0 && r < i)
            r = i;

        if (g > 0 && g < i)
            g = i;

        if (b > 0 && b < i)
            b = i;

        return ofRGBA(
            Math.min((int) (r / (1 / factor)), 255),
            Math.min((int) (g / (1 / factor)), 255),
            Math.min((int) (b / (1 / factor)), 255),
            getAlpha()
        );
    }

    public Color darker(float factor) {
        return ofRGBA(
            Math.max((int) (getRed() * (1 / factor)), 0),
            Math.max((int) (getGreen() * (1 / factor)), 0),
            Math.max((int) (getBlue() * (1 / factor)), 0),
            getAlpha()
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (getClass() != other.getClass())
            return false;

        return hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return argbInt;
    }

    @Override
    public String toString() {
        return String.valueOf(argbInt);
    }
}
