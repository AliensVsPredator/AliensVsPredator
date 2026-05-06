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
     * {@code sampler2D entityMask} — RG8, sampled with screen-space texCoord.
     * <p>
     * <b>R channel:</b> per-fragment category byte written by patched render-type shaders:
     * <ul>
     * <li>{@code 1.00} — entity (incl. armor, eyes, held items rendered through entity render types)</li>
     * <li>{@code 0.50} — terrain (chunk meshes:
     * solid/cutout/cutout_mipped/translucent/tripwire/translucent_moving)</li>
     * <li>{@code 0.25} — particle</li>
     * <li>{@code 0.00} — unpatched fragment (sky, GUI, anything the framework didn't capture)</li>
     * </ul>
     * Use threshold steps to recover categories — e.g. {@code step(0.75, mask.r)} for entity-only,
     * {@code step(0.125, mask.r)} for "anything captured."
     * <p>
     * <b>G channel:</b> background-entity flag. {@code 1.0} on entity fragments drawn while a downstream mod
     * pushed {@code BLibPostEffectFramework.pushBackgroundEntity()}; {@code 0.0} otherwise. Lets a vision-style
     * post-effect render flagged entities through its world-coloring branch instead of its foreground-entity
     * branch (so e.g. a mob outside the vision's visibility tag still draws but blends with the world). Unrelated
     * to {@code MobEffects.INVISIBILITY} — that's still handled separately by vanilla.
     * <p>
     * Requires the framework's MainTarget-MRT mixin to be active (i.e., not running under Iris/Oculus).
     */
    ENTITY_MASK,

    /**
     * {@code sampler2D entityLightmap} — RGBA8, sampled with screen-space texCoord. The lightmap value sampled at the
     * entity's per-fragment lightmap coord during its original draw. Black for non-entity pixels. Same MRT requirement
     * as {@link #ENTITY_MASK}.
     */
    ENTITY_LIGHTMAP,

    /**
     * {@code sampler2D entityNormal} — RGBA8, sampled with screen-space texCoord. The entity fragment's vanilla shader
     * normal, packed as {@code rgb = n * 0.5 + 0.5} (the standard {@code [-1, 1]} → {@code [0, 1]} mapping); unpack
     * with {@code n = normalize(rgb * 2.0 - 1.0)}. Alpha is {@code 1.0} on entity fragments and {@code 0.0} elsewhere —
     * equivalent to the entity mask, useful for branchless gating. Black on non-entity pixels (so an unguarded unpack
     * yields {@code (-1, -1, -1)}; always gate with {@link #ENTITY_MASK} or the alpha channel before using). Shaders
     * without a {@code Normal} vertex attribute (e.g. {@code rendertype_eyes}) write a Y-up fallback. Same MRT
     * requirement as {@link #ENTITY_MASK}.
     */
    ENTITY_NORMAL,

    /**
     * {@code sampler2D entityDrawData} — RGBA8, sampled with screen-space texCoord. Captures per-fragment draw-time
     * data that is otherwise lost before the post pass:
     * <ul>
     * <li>R: source-texture detail, the raw red channel of the texel ({@code Sampler0.r} pre-modulation).</li>
     * <li>G: raw normalized block-light coord, {@code UV2.x / 240}.</li>
     * <li>B: raw normalized sky-light coord, {@code UV2.y / 240}.</li>
     * <li>A: vanilla diffuse face-light term derived from {@code Normal}, {@code Light0_Direction}, and
     * {@code Light1_Direction} when those are available, with synthetic-key-light or flat fallbacks.</li>
     * </ul>
     * Effects that need lightmap-derived light values can re-derive them from {@link #ENTITY_LIGHTMAP}; this attachment
     * keeps the G/B channels as raw light coords so it stays useful for non-thermal post-effects too.
     * <p>
     * Same MRT requirement as {@link #ENTITY_MASK}.
     */
    ENTITY_DRAW_DATA,

    /**
     * {@code sampler2D entitySpecular} — RGBA8, sampled with screen-space texCoord. Optional per-fragment PBR/specular
     * data captured during entity rendering, packed using LabPBR-style conventions:
     * <ul>
     * <li>R: smoothness / inverse-roughness raw sample (passthrough).</li>
     * <li>G: roughness / "JCL specular_pixel.g" — drives the JCL PBR heat-bias term.</li>
     * <li>B: metalness / F0 raw sample (passthrough).</li>
     * <li>A: emission / "JCL specular_pixel.a" — drives the JCL PBR base-heat term.</li>
     * </ul>
     * For vanilla entity shaders that do not declare an {@code EntitySpecular} sampler this attachment stays at
     * {@code (0, 0, 0, 0)} and the thermal post shader falls back to its non-PBR branch. A downstream mod that wires a
     * real specular texture binding into the entity render path can declare {@code uniform sampler2D EntitySpecular} in
     * its fragment source and the patcher will sample it automatically.
     * <p>
     * Same MRT requirement as {@link #ENTITY_MASK}.
     */
    ENTITY_SPECULAR,

    /**
     * {@code sampler2D entityMaterialId} — R8, sampled with screen-space texCoord. JCL-style {@code ipbr_id} hook — the
     * patched fragment shader reads an optional {@code uniform int BlibMaterialId;} (defaulting to 0 when absent or
     * unset) and writes {@code float(BlibMaterialId & 0xFF) / 255.0} into this attachment. The thermal post shader uses
     * the resulting [0, 1] sample to apply per-material heat additions analogous to JCL's
     * {@code abs(ipbr_id - 10032.) < .5} branches.
     * <p>
     * For vanilla entity shaders the patcher detects the optional uniform and writes 0 if it isn't declared, so this
     * attachment is effectively zero everywhere until a downstream mod injects a {@code BlibMaterialId} uniform into
     * its render-state setup. The attachment is allocated regardless so post-effects requesting it always have a stable
     * sampler binding.
     * <p>
     * Same MRT requirement as {@link #ENTITY_MASK}.
     */
    ENTITY_MATERIAL_ID
}
