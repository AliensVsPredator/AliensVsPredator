package com.blib.api.client.shader.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Declarative description of a post-effect: identity, fragment-shader location, requested framework inputs,
 * effect-specific uniform bindings, an active-state supplier, and a compositing priority.
 * <p>
 * The fragment shader resource is read from {@code assets/<id.namespace>/shaders/blib_post/<id.path>.fsh} relative to
 * the {@code fragmentShader} location; it should {@code #include "/blib_post/std.glsl"} to pick up the framework's
 * standard uniforms and sampler bindings.
 * <p>
 * Effects compose by priority ascending — higher-priority effects run later and composite atop lower ones.
 */
public record BLibPostEffectSpec(
    ResourceLocation id,
    ResourceLocation fragmentShader,
    Set<BLibPostEffectInput> inputs,
    List<BLibPostEffectUniform> uniforms,
    BooleanSupplier enabledWhen,
    int priority
) {

    public BLibPostEffectSpec {
        inputs = inputs.isEmpty() ? Set.of() : EnumSet.copyOf(inputs);
        uniforms = uniforms.isEmpty() ? List.of() : List.copyOf(uniforms);
    }

    public static Builder builder(ResourceLocation id, ResourceLocation fragmentShader) {
        return new Builder(id, fragmentShader);
    }

    public static final class Builder {

        private final ResourceLocation id;

        private final ResourceLocation fragmentShader;

        private final EnumSet<BLibPostEffectInput> inputs = EnumSet.noneOf(BLibPostEffectInput.class);

        private final List<BLibPostEffectUniform> uniforms = new ArrayList<>();

        private BooleanSupplier enabledWhen = () -> true;

        private int priority;

        private Builder(ResourceLocation id, ResourceLocation fragmentShader) {
            this.id = id;
            this.fragmentShader = fragmentShader;
        }

        public Builder withInput(BLibPostEffectInput input) {
            this.inputs.add(input);
            return this;
        }

        public Builder withUniform(BLibPostEffectUniform uniform) {
            this.uniforms.add(uniform);
            return this;
        }

        public Builder enabledWhen(BooleanSupplier supplier) {
            this.enabledWhen = supplier;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public BLibPostEffectSpec build() {
            return new BLibPostEffectSpec(id, fragmentShader, inputs, uniforms, enabledWhen, priority);
        }
    }
}
