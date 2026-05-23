package com.blib.internal.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Cross-loader-friendly handle to a custom GLSL shader.
 * <p>
 * Each {@link BLibShader} is declared once at startup (added to {@link BLibShaders#ALL}) and is then resolved by the
 * loader-specific shader-registration glue at the appropriate point in the resource-load lifecycle. The
 * {@link ShaderInstance} reference is populated by the loader code via {@link #setInstance(ShaderInstance)}; consumers
 * read it via {@link #instance()} (returning {@code null} until the first registration completes — guard your render
 * paths accordingly).
 */
@ApiStatus.Internal
public final class BLibShader {

    private final ResourceLocation id;

    private final VertexFormat vertexFormat;

    private @Nullable ShaderInstance instance;

    /**
     * @param id           the shader id, used by MC's shader system to find
     *                     {@code assets/<namespace>/shaders/core/<path>.{vsh,fsh,json}}
     * @param vertexFormat the vertex format the shader expects; must match the format of any draw using this shader
     */
    public BLibShader(ResourceLocation id, VertexFormat vertexFormat) {
        this.id = id;
        this.vertexFormat = vertexFormat;
    }

    public ResourceLocation id() {
        return id;
    }

    public VertexFormat vertexFormat() {
        return vertexFormat;
    }

    public @Nullable ShaderInstance instance() {
        return instance;
    }

    public void setInstance(ShaderInstance instance) {
        this.instance = instance;
    }

    /** Convenience supplier matching {@code RenderSystem.setShader}'s argument shape. */
    public Supplier<ShaderInstance> supplier() {
        return () -> instance;
    }

    /** Returns the shader's named uniform, or {@code null} if the shader isn't loaded yet or the uniform is absent. */
    public @Nullable Uniform uniform(String name) {
        if (instance == null) {
            return null;
        }

        return instance.getUniform(name);
    }
}
