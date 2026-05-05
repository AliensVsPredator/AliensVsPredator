package com.blib.api.client.shader.v1;

import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Effect-specific uniform binding. The framework resolves the supplier each frame and pushes the value to the effect's
 * shader before invoking its draw. Uniform names must match a {@code uniform} declaration in the effect's fragment
 * shader.
 * <p>
 * Standard uniforms (time, partialTick, sunAngle, nightVision, etc.) are provided automatically — see
 * {@code assets/blib/shaders/blib_post/std.glsl} — and need not be added here.
 */
public sealed interface BLibPostEffectUniform {

    String name();

    record Float1(
        String name,
        FloatSupplier value
    ) implements BLibPostEffectUniform {}

    record Float2(
        String name,
        Supplier<Vector2f> value
    ) implements BLibPostEffectUniform {}

    record Float3(
        String name,
        Supplier<Vector3f> value
    ) implements BLibPostEffectUniform {}

    record Float4(
        String name,
        Supplier<Vector4f> value
    ) implements BLibPostEffectUniform {}

    record Int1(
        String name,
        IntSupplier value
    ) implements BLibPostEffectUniform {}

    record Matrix4(
        String name,
        Supplier<Matrix4f> value
    ) implements BLibPostEffectUniform {}

    record Texture(
        String name,
        Supplier<ResourceLocation> value,
        int unit
    ) implements BLibPostEffectUniform {}

    @FunctionalInterface
    interface FloatSupplier {

        float getAsFloat();
    }
}
