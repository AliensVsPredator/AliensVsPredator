package com.blib.internal.client.shader;

import org.jetbrains.annotations.ApiStatus;

/**
 * Shared mode flag for the thermal-debug post-effect. {@code 0} disables the debug overlay; non-zero values pick which
 * captured MRT channel is visualized over the scene. The post-effect's {@code BooleanSupplier enabledWhen} reads
 * {@link #isActive()}; its fragment shader reads {@link #mode()} via a uniform.
 * <p>
 * Modes mirror the captured payloads documented in {@code BLibPostEffectInput} and
 * {@code JCL_THERMAL_PARITY_REQUIREMENTS.md}:
 * <ol>
 * <li>{@code entityMask} as white-on-black.</li>
 * <li>{@code entityLightmap.rgb}.</li>
 * <li>{@code entityNormal} unpacked back to {@code [0, 1]} for display.</li>
 * <li>{@code entityThermalData.r} (detail) as grayscale.</li>
 * <li>{@code entityThermalData.g} (raw block-light coord) as grayscale.</li>
 * <li>{@code entityThermalData.b} (raw sky-light coord) as grayscale.</li>
 * <li>{@code entityThermalData.a} (vanilla diffuse face-light) as grayscale.</li>
 * <li>{@code entitySpecular.g} (roughness, JCL PBR g-channel) as grayscale.</li>
 * <li>{@code entitySpecular.a} (emission, JCL PBR a-channel) as grayscale.</li>
 * <li>{@code entityMaterialId} (JCL ipbr_id byte) as grayscale.</li>
 * </ol>
 * <p>
 * For entities, the values displayed in modes 5/6/7 (block-light, sky-light, face-light) are sampled <em>per bone</em>
 * by the {@link com.blib.internal.client.posteffect.BLibPerBoneLightContext}-driven mixin into
 * {@code ModelPart.compile} — different cubes of a mob can read different values when the mob spans a lighting
 * boundary.
 * <p>
 * Volatile because the command can run on the integrated-server thread while the render thread reads it.
 */
@ApiStatus.Internal
public final class BLibThermalDebugState {

    public static final int MODE_OFF = 0;

    public static final int MODE_ENTITY_MASK = 1;

    public static final int MODE_ENTITY_LIGHTMAP = 2;

    public static final int MODE_ENTITY_NORMAL = 3;

    public static final int MODE_THERMAL_DETAIL = 4;

    public static final int MODE_THERMAL_BLOCK_LIGHT = 5;

    public static final int MODE_THERMAL_SKY = 6;

    public static final int MODE_THERMAL_FACE = 7;

    public static final int MODE_SPECULAR_ROUGHNESS = 8;

    public static final int MODE_SPECULAR_EMISSION = 9;

    public static final int MODE_MATERIAL_ID = 10;

    public static final int MODE_MAX = MODE_MATERIAL_ID;

    private static volatile int mode;

    private BLibThermalDebugState() {
        throw new UnsupportedOperationException();
    }

    public static boolean isActive() {
        return mode != MODE_OFF;
    }

    public static int mode() {
        return mode;
    }

    public static void setMode(int newMode) {
        if (newMode < MODE_OFF || newMode > MODE_MAX) {
            throw new IllegalArgumentException("Thermal debug mode out of range: " + newMode);
        }

        mode = newMode;
    }
}
