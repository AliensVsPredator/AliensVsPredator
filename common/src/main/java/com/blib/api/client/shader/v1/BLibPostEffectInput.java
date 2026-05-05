package com.blib.api.client.shader.v1;

/**
 * Optional inputs a post-effect's fragment shader can request. Each input maps to a fixed sampler binding name (shown
 * in parens below) that the framework wires automatically when the effect runs. Effects only request the inputs they
 * actually use; unused MRT attachments aren't allocated when no active effect needs them.
 */
public enum BLibPostEffectInput {

    /** {@code sampler2D colortex0} — current scene color (the framebuffer up to the moment this effect runs). */
    COLOR_TEXTURE,

    /** {@code sampler2D depthtex0} — current scene depth, sampled with screen-space texCoord. */
    DEPTH_TEXTURE,

    /**
     * {@code sampler2D lightmap} — Minecraft's {@code LightTexture} (a 16x16 LUT). Sampled with lightmap coordinates
     * (block-light on x, sky-light on y); has no per-fragment screen-space form on its own. Combine with
     * {@link #ENTITY_LIGHTMAP} for entities, or with heuristic warm-pixel detection in {@link #COLOR_TEXTURE} for world
     * heat sources.
     */
    LIGHTMAP_TEXTURE,

    /**
     * {@code sampler2D entityMask} — R8, sampled with screen-space texCoord. {@code 1.0} where an entity fragment was
     * drawn during the level pass, {@code 0.0} elsewhere. Requires the framework's MainTarget-MRT mixin to be active
     * (i.e., not running under Iris/Oculus).
     */
    ENTITY_MASK,

    /**
     * {@code sampler2D entityLightmap} — RGBA8, sampled with screen-space texCoord. The lightmap value sampled at the
     * entity's per-fragment lightmap coord during its original draw. Black for non-entity pixels. Same MRT requirement
     * as {@link #ENTITY_MASK}.
     */
    ENTITY_LIGHTMAP
}
