package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.shaders.Program;
import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blib.mod.BLib;

/**
 * Source-string transformer that injects MRT auxiliary outputs into vanilla entity-, terrain- and particle-family
 * shaders so the rest of the post-effect framework can read per-fragment "what category is this", "lightmap value",
 * "surface normal", "thermal source data", and (optional, defaults to zero) "specular/PBR data" without a re-render
 * pass.
 * <p>
 * Vertex side: declares a {@code blib_lightmap} varying and writes {@code texelFetch(Sampler2, UV2 / 16, 0)} to it (or
 * {@code vec4(0)} for shaders without {@code UV2}). Declares a {@code blib_normal} varying and writes the vanilla
 * shader {@code Normal} (or {@code vec3(0, 1, 0)} for shaders without one, e.g. particles or {@code rendertype_eyes}).
 * Captures raw normalized light coordinates and a face-light term — using vanilla {@code Light0_Direction} +
 * {@code Light1_Direction} when available (entities), or a synthetic key direction against the captured {@code Normal}
 * for shaders that have a normal but no light uniforms (terrain), or a flat {@code 1.0} for particles.
 * <p>
 * Fragment side: turns the lone {@code out vec4 fragColor;} declaration into six explicitly-located outputs (color,
 * mask, lightmap, normal, thermal data, specular). Writes a per-category mask value (entities {@code 1.0}, terrain
 * {@code 0.5}, particles {@code 0.25}; non-patched fragments stay {@code 0.0}), the varying lightmap value, the
 * renormalized {@code 0.5 * n + 0.5}-packed normal, draw-time thermal source data, and a specular sample (or zero when
 * no {@code EntitySpecular} sampler is declared).
 * <p>
 * If the source doesn't match the expected anchors (e.g., Mojang restructures shaders in a future version), the patcher
 * returns the source unchanged and logs a warning rather than producing non-compilable GLSL.
 * <p>
 * Bypassed entirely when {@link BLibIrisCompat#isShaderModActive()} — Iris owns those shaders in that case.
 */
@ApiStatus.Internal
public final class BLibEntityShaderPatcher {

    /** Per-fragment category written to the {@code entityMask} R8 attachment. */
    public enum Category {

        /** Living/inanimate entity, including held items and armor. Mask value {@code 1.0}. */
        ENTITY(1.0F),

        /** Terrain block render (solid/cutout/translucent/tripwire/moving block). Mask value {@code 0.5}. */
        TERRAIN(0.5F),

        /** Vanilla particle. Mask value {@code 0.25}. */
        PARTICLE(0.25F),

        /**
         * Sun and moon texture (vanilla {@code position_tex} during the level pass). Mask value {@code 0.0625} — low
         * enough to fall under the post shader's "sky/passthrough" threshold but distinguishable from pure sky
         * ({@code 0.0}) so the thermal post can recolor the texture with the heat gradient (white-hot for the sun,
         * yellow for the moon) while still letting the texture itself drive luminance detail. Sun/moon are drawn
         * with alpha blending; the framebuffer's blend factor (SRC_ALPHA) applied to attachment 1 means the mask
         * value at celestial-edge pixels naturally interpolates between {@code 0.0625} (fully texture) and
         * {@code 0.0} (pure sky behind), giving the post shader a continuous "celestial alpha" signal it can use
         * to fade the colored texture smoothly into the ambient sky color at the texture edges.
         */
        CELESTIAL(0.0625F),

        /**
         * Sky/cloud/lightning/end-portal shaders — draws to MainTarget but contributes no thermal data. Patched to
         * write deterministic zero to all auxiliary attachments. Without this, an unpatched shader's single
         * {@code out vec4 fragColor} leaves attachments 1-6 as spec-undefined writes; on some drivers that leaks
         * structured garbage (e.g. interpolated attribute values) into {@code entityMask} and
         * {@code entityDrawData}, which the post shader then interprets as terrain heat — producing the
         * green/blue ring banding around the player and concentric rings around the sun/moon. Mask value
         * {@code 0.0} — same as the cleared default, so the post shader treats these pixels as ambient sky.
         */
        PASSTHROUGH(0.0F);

        public final float maskValue;

        Category(float maskValue) {
            this.maskValue = maskValue;
        }
    }

    private static final Pattern OUT_FRAG_COLOR = Pattern.compile("(?m)^\\s*out\\s+vec4\\s+fragColor\\s*;\\s*$");

    private static final Pattern UV2_DECL = Pattern.compile("(?m)^\\s*in\\s+ivec2\\s+UV2\\s*;\\s*$");

    private static final Pattern SAMPLER2_DECL = Pattern.compile("(?m)^\\s*uniform\\s+sampler2D\\s+Sampler2\\s*;\\s*$");

    private static final Pattern NORMAL_DECL = Pattern.compile("(?m)^\\s*in\\s+vec3\\s+Normal\\s*;\\s*$");

    private static final Pattern LIGHT0_DECL = Pattern.compile("(?m)^\\s*uniform\\s+vec3\\s+Light0_Direction\\s*;\\s*$");

    private static final Pattern LIGHT1_DECL = Pattern.compile("(?m)^\\s*uniform\\s+vec3\\s+Light1_Direction\\s*;\\s*$");

    private static final Pattern ENTITY_SPECULAR_DECL = Pattern.compile(
        "(?m)^\\s*uniform\\s+sampler2D\\s+EntitySpecular\\s*;\\s*$"
    );

    private static final Pattern BLIB_MATERIAL_ID_DECL = Pattern.compile(
        "(?m)^\\s*uniform\\s+int\\s+BlibMaterialId\\s*;\\s*$"
    );

    private static final Pattern TEXCOORD0_DECL = Pattern.compile("(?m)^\\s*in\\s+vec2\\s+texCoord0\\s*;\\s*$");

    private static final Pattern SAMPLER0_DECL = Pattern.compile("(?m)^\\s*uniform\\s+sampler2D\\s+Sampler0\\s*;\\s*$");

    private static final Pattern MAIN_OPEN = Pattern.compile("void\\s+main\\s*\\(\\s*\\)\\s*\\{");

    private static final Pattern VERSION_DIRECTIVE = Pattern.compile("(?m)^\\s*#version\\s+\\d+\\s*$");

    private static final java.util.Set<String> LOGGED_NAMES = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private BLibEntityShaderPatcher() {
        throw new UnsupportedOperationException();
    }

    public static String transform(String shaderName, Program.Type type, String source) {
        if (BLibIrisCompat.isShaderModActive()) {
            return source;
        }

        var category = categoryFor(shaderName);

        if (category == null) {
            return source;
        }

        if (LOGGED_NAMES.add(shaderName + ":" + type.getName())) {
            BLib.LOGGER.info("[BLib] Patching {} shader '{}' as {}", type.getName(), shaderName, category);
        }

        try {
            if (category == Category.PASSTHROUGH || category == Category.CELESTIAL) {
                // Vertex stage stays untouched — these fragments don't read any blib_* varyings, so adding matching
                // `out` declarations on the vertex side would be wasted work and would risk linker mismatches with
                // shaders that have unusual vertex-output layouts. Both categories use the same minimal-zero patch
                // shape; only the mask byte differs.
                return type == Program.Type.FRAGMENT ? patchFragmentMinimal(source, category) : source;
            }
            return switch (type) {
                case VERTEX -> patchVertex(source, category);
                case FRAGMENT -> patchFragment(source, category);
            };
        } catch (Exception e) {
            BLib.LOGGER.warn("[BLib] Failed to MRT-patch {} shader '{}': {}", type.getName(), shaderName, e.getMessage());
            return source;
        }
    }

    public static @org.jetbrains.annotations.Nullable Category categoryFor(String name) {
        if (name.startsWith("rendertype_entity_")) {
            // Shadow and glint are alpha-blended overlays drawn AFTER the underlying entity. Patching them with
            // their own mask byte (or even a passthrough-zero) blends with the entity's mask=1.0 underneath and
            // corrupts the entity classification. Leave them unpatched here; BLibGbufferUniforms's per-shader
            // colorMask toggle suppresses their writes to the auxiliary attachments at draw time, so the
            // underlying entity's data is preserved exactly.
            if (name.equals("rendertype_entity_shadow")
                || name.equals("rendertype_entity_glint")
                || name.equals("rendertype_entity_glint_direct")) {
                return null;
            }
            return Category.ENTITY;
        }

        if (name.startsWith("rendertype_armor_")) {
            // Armor glint is the same overlay-blending case as entity glint.
            if (name.contains("glint")) {
                return null;
            }
            return Category.ENTITY;
        }

        // rendertype_eyes draws the glowing-eye overlay AFTER the body via additive blending
        // ({@code SRC_ALPHA, ONE}). The patcher's mask write forces source alpha to 1.0 to survive alpha-blended
        // render types (entity_translucent etc.) — but on an additive type, that same forced 1.0 means every eye
        // fragment ADDS its mask byte and lane-encoding into the body's already-written values, corrupting the
        // mask byte and (for predator vision) packing both lanes into the result. Leave it unpatched: the
        // body has already written valid mask data for those pixels, and {@link BLibGbufferUniforms#toggleAuxColorMask}
        // will suppress eye writes to attachments 1-6 entirely while still letting the eye color land in attachment 0.

        // Terrain render types — every chunk-mesh family. Excludes specialized water_mask/beacon_beam (they have
        // unusual attribute layouts and are rare enough that a missing thermal contribution is a non-issue).
        if (
            name.equals("rendertype_solid")
                || name.equals("rendertype_cutout")
                || name.equals("rendertype_cutout_mipped")
                || name.equals("rendertype_translucent")
                || name.equals("rendertype_tripwire")
                || name.equals("rendertype_translucent_moving_block")
        ) {
            return Category.TERRAIN;
        }

        if (name.equals("particle")) {
            return Category.PARTICLE;
        }

        // Sun and moon — both drawn with `position_tex` during {@code LevelRenderer.renderSky} (no other level-pass
        // use of `position_tex` writes to MainTarget at sky-rendering time). Tagged separately from generic
        // passthrough so the post shader can identify celestial-body fragments and recolor them via the heat
        // gradient (white-hot sun, yellow moon) while still letting the texture itself drive per-pixel detail.
        if (name.equals("position_tex")) {
            return Category.CELESTIAL;
        }

        // Passthrough — sky-stage shaders that draw to MainTarget BEFORE terrain/entities and so don't blend on
        // top of any already-categorized pixels. Patched to explicitly zero the auxiliary attachments so unwritten
        // outputs don't leave undefined data behind (the GL spec leaves writes to attachments 1-6 undefined for
        // shaders that only declare `out vec4 fragColor;`; on some drivers that leaks structured garbage —
        // interpolated attribute remnants — into the auxiliary attachments and produces concentric ring banding
        // in the sky). {@code position} draws the sky disc and stars; {@code position_color},
        // {@code position_color_lightmap}, {@code position_tex_color}, {@code position_color_tex_lightmap}
        // draw the void plane, sunrise/sunset gradient, and various world-space overlays drawn during the sky
        // stage. {@code rendertype_clouds}, {@code rendertype_lightning}, {@code rendertype_end_portal} draw
        // their respective effects.
        // <p>
        // OVERLAY shaders that draw AFTER terrain/entities with alpha blending (e.g. block-outline
        // {@code rendertype_lines}, {@code rendertype_entity_shadow}, glints, {@code rendertype_leash},
        // {@code rendertype_crumbling}, etc.) are NOT in this list — patching them as passthrough would write
        // mask=0 on top of the underlying terrain's mask=0.5, blend to a fractional value, and route those pixels
        // through the wrong heat-formula branch (square sky-colored shadows on the ground, flat-blue block
        // outlines, etc.). Instead they're left unpatched (categoryFor returns null) and BLibGbufferUniforms
        // disables writes to attachments 1-6 at draw time so the underlying classified data is preserved.
        if (
            name.equals("position")
                || name.equals("position_color")
                || name.equals("position_tex_color")
                || name.equals("position_color_lightmap")
                || name.equals("position_color_tex_lightmap")
                || name.equals("rendertype_clouds")
                || name.equals("rendertype_lightning")
                || name.equals("rendertype_end_portal")
        ) {
            return Category.PASSTHROUGH;
        }

        return null;
    }

    private static String patchVertex(String source, Category category) {
        var canSampleLightmap = UV2_DECL.matcher(source).find() && SAMPLER2_DECL.matcher(source).find();
        var hasLightCoords = UV2_DECL.matcher(source).find();
        var hasNormal = NORMAL_DECL.matcher(source).find();
        var hasVanillaLights = LIGHT0_DECL.matcher(source).find() && LIGHT1_DECL.matcher(source).find();

        var lightmapWrite = canSampleLightmap
            ? "    blib_lightmap = texelFetch(Sampler2, UV2 / 16, 0);\n"
            : "    blib_lightmap = vec4(0.0);\n";

        var lightCoordWrite = hasLightCoords
            ? "    blib_lightCoord = clamp(vec2(UV2) / 240.0, 0.0, 1.0);\n"
            : "    blib_lightCoord = vec2(0.0);\n";

        var normalWrite = hasNormal
            ? "    blib_normal = Normal;\n"
            : "    blib_normal = vec3(0.0, 1.0, 0.0);\n";

        // Face-light fallback chain:
        // 1. vanilla Light0/Light1 dot products (entities — closest to vanilla shading).
        // 2. synthetic key-light dot product against the captured Normal (terrain — gives per-face shading despite
        // the absence of vanilla light-direction uniforms).
        // 3. flat 1.0 (particles or shaders without a Normal — they have no usable face geometry).
        String faceLightWrite;
        if (hasNormal && hasVanillaLights) {
            faceLightWrite =
                "    float blib_l0 = max(0.0, dot(Light0_Direction, Normal));\n"
                    + "    float blib_l1 = max(0.0, dot(Light1_Direction, Normal));\n"
                    + "    blib_faceLight = min(1.0, (blib_l0 + blib_l1) * 0.6 + 0.4);\n";
        } else if (hasNormal) {
            // Synthetic light direction — close to the vanilla Light0_Direction default (top-front).
            faceLightWrite =
                "    vec3 blib_keyDir = normalize(vec3(0.4, 0.8, 0.5));\n"
                    + "    blib_faceLight = clamp(dot(normalize(Normal), blib_keyDir) * 0.5 + 0.5, 0.0, 1.0);\n";
        } else {
            faceLightWrite = "    blib_faceLight = 1.0;\n";
        }

        var categoryComment = "    // BLib MRT category: " + category.name() + "\n";

        var newDecls =
            "out vec4 blib_lightmap;\n"
                + "out vec3 blib_normal;\n"
                + "out vec2 blib_lightCoord;\n"
                + "out float blib_faceLight;\n";

        var withDecls = insertBeforeMain(source, newDecls);
        return insertAtMainStart(
            withDecls,
            categoryComment + lightmapWrite + lightCoordWrite + normalWrite + faceLightWrite
        );
    }

    private static String patchFragment(String source, Category category) {
        var matcher = OUT_FRAG_COLOR.matcher(source);

        if (!matcher.find()) {
            return source;
        }

        // Single-channel attachments (mask R8 at loc 1, materialId R8 at loc 6) get declared as `out vec4` rather
        // than `out float`. Reason: when alpha-blended render types like rendertype_entity_translucent (used by
        // PlayerModel-derived mobs — piglins, zombie piglins, players) draw, the GL blend factor SRC_ALPHA is
        // evaluated for every color attachment. For `out float` outputs the alpha component is implementation-
        // defined per the GL spec — some drivers correctly return 1.0, others return 0, which silently zeros out
        // the mask write and makes the entity invisible in thermal mode. Declaring as vec4 with explicit alpha=1.0
        // in every write site (see writes block below) guarantees the source alpha is 1.0 across drivers; only the
        // R component lands in the actual R8 storage.
        var replacement =
            "in vec4 blib_lightmap;\n"
                + "in vec3 blib_normal;\n"
                + "in vec2 blib_lightCoord;\n"
                + "in float blib_faceLight;\n"
                + "uniform int BlibHeldItem;\n"
                + "uniform int BlibBackgroundEntity;\n"
                + "uniform int BlibBackgroundEntity2;\n"
                + "layout(location = 0) out vec4 fragColor;\n"
                + "layout(location = 1) out vec4 blib_entityMask;\n"
                + "layout(location = 2) out vec4 blib_entityLightmap;\n"
                + "layout(location = 3) out vec4 blib_entityNormal;\n"
                + "layout(location = 4) out vec4 blib_entityDrawData;\n"
                + "layout(location = 5) out vec4 blib_entitySpecular;\n"
                + "layout(location = 6) out vec4 blib_entityMaterialId;\n";

        var withOuts = matcher.replaceFirst(Matcher.quoteReplacement(replacement));

        // GLSL 150 doesn't include `layout(location = N)` on outs without an extension; backport via the ARB.
        var withExt = insertAfterVersion(withOuts, "#extension GL_ARB_explicit_attrib_location : require\n");

        // Capture both untinted detail AND a warm-color heuristic from raw Sampler0 RGB. `color` (the local in vanilla
        // entity shaders) has been multiplied by `lightMapColor` and `ColorModulator` by the bottom of main(), so it
        // is biome/dimension-tinted; Sampler0 is the raw texel and is biome-independent.
        // detail → entityDrawData.r (used for texture-variation subtraction)
        // warmth → entitySpecular.a (LabPBR emission slot — see specular write below)
        // The warmth math is the inline form of the post-shader's old warmColorHeat() helper, biased so it returns
        // ~1.0 for dominant-red textures (lava, fire, magma, redstone, glowstone hot spots).
        var canSampleBaseColor = SAMPLER0_DECL.matcher(source).find()
            && TEXCOORD0_DECL.matcher(source).find();
        var baseColorWrite = canSampleBaseColor
            ? "    vec3 blib_baseRGB = texture(Sampler0, texCoord0).rgb;\n"
                + "    float blib_detail = clamp(blib_baseRGB.r, 0.0, 1.0);\n"
                + "    float blib_redDom = max(blib_baseRGB.r - max(blib_baseRGB.g, blib_baseRGB.b) * 0.6, 0.0);\n"
                + "    float blib_warmAx = max(blib_baseRGB.r * 0.7 + blib_baseRGB.g * 0.3 - blib_baseRGB.b, 0.0);\n"
                + "    float blib_warmHeur = clamp(blib_redDom * 1.6 + blib_warmAx * 0.6, 0.0, 1.0);\n"
            : "    float blib_detail = clamp(color.r, 0.0, 1.0);\n"
                + "    float blib_warmHeur = 0.0;\n";

        // Optional PBR/specular sampling. Vanilla shaders do not declare an EntitySpecular sampler, so we fall back
        // to packing the raw warm-heuristic into specular.a (the LabPBR emission slot — JCL's `specular_pixel.a`).
        // The post shader gates this by very-high block light so non-emissive warm-colored textures (e.g. red wool
        // away from a torch) don't read as heat sources. A downstream mod that adds `uniform sampler2D EntitySpecular`
        // (with a parallel render-state binding) will get the real LabPBR data instead.
        var canSampleSpecular = ENTITY_SPECULAR_DECL.matcher(source).find()
            && TEXCOORD0_DECL.matcher(source).find();
        var specularWrite = canSampleSpecular
            ? "    blib_entitySpecular = texture(EntitySpecular, texCoord0);\n"
            : "    blib_entitySpecular = vec4(0.0, 0.0, 0.0, blib_warmHeur);\n";

        // Optional material-ID hook (JCL ipbr_id equivalent). Vanilla shaders do not declare a BlibMaterialId uniform,
        // so the write defaults to 0 and the post shader sees a flat zero ID buffer. A downstream mod that injects the
        // uniform into a shader's source AND wires a per-draw setter (via the public ShaderInstance API) will get
        // their material IDs encoded at byte resolution into the entityMaterialId attachment automatically.
        var canSampleMaterialId = BLIB_MATERIAL_ID_DECL.matcher(source).find();
        var materialIdWrite = canSampleMaterialId
            ? "    blib_entityMaterialId = vec4(float(BlibMaterialId & 0xFF) / 255.0, 0.0, 0.0, 1.0);\n"
            : "    blib_entityMaterialId = vec4(0.0, 0.0, 0.0, 1.0);\n";

        // LPV voxelization is now in the vertex shader (see patchVertex above) — fragment-stage imageStore was
        // silently dropped by the chunk-rendering path on the test driver while same-shader held-item fragments
        // wrote successfully. Vertex-stage writes are JCL's approach and are far more driver-portable.
        var maskLiteral = String.format(java.util.Locale.ROOT, "%.4f", category.maskValue);

        // `length() > 0.0` guards against the rare case where the interpolated normal collapses to zero.
        // entityDrawData channel layout (all biome/dimension-independent):
        // R = detail = clamp(texture(Sampler0, texCoord0).r, 0, 1) — untinted texel red
        // G = raw normalized block-light coord (UV2.x / 240)
        // B = raw normalized sky-light coord (UV2.y / 240)
        // A = face-light from Normal vs Light0/Light1 (entities), synthetic key-light (terrain), or 1.0 (particles)
        // Held-item override: BlibHeldItem is set to 1 by the Java side (BLibGbufferUniforms via the
        // ShaderInstance.apply mixin) only while a first-person hand or third-person ItemInHandLayer draw is in
        // flight. The 0.875 mask value is decoded by the thermal post shader as "passthrough — output src.rgb,
        // skip thermal recoloring" so held items remain readable in IR mode.
        // Background-entity override: two independent lanes (BlibBackgroundEntity = lane A, BlibBackgroundEntity2 =
        // lane B) are set by the Java side around entity draws that should render as part of the world rather than
        // as foreground entities. The two lane states are packed into entityMask.g as 0.25 * laneA + 0.5 * laneB,
        // so the four combinations land at 0.0 / 0.25 / 0.5 / 0.75 — distinguishable under NEAREST sampling of the
        // RG8 attachment. Consumers can either decode per-lane (see e.g. predator vision's wipe-aware shader) or,
        // for legacy "any background" behavior, treat (mask.g > 0.125) as the single background flag.
        var writes =
            "    float blib_maskValue = (BlibHeldItem != 0) ? 0.875 : " + maskLiteral + ";\n"
                + "    float blib_bgLaneA = (BlibBackgroundEntity != 0) ? 1.0 : 0.0;\n"
                + "    float blib_bgLaneB = (BlibBackgroundEntity2 != 0) ? 1.0 : 0.0;\n"
                + "    float blib_backgroundFlag = blib_bgLaneA * 0.25 + blib_bgLaneB * 0.5;\n"
                + "    blib_entityMask = vec4(blib_maskValue, blib_backgroundFlag, 0.0, 1.0);\n"
                + "    blib_entityLightmap = blib_lightmap;\n"
                + "    vec3 blib_n = blib_normal;\n"
                + "    blib_n = length(blib_n) > 0.0 ? normalize(blib_n) : vec3(0.0, 1.0, 0.0);\n"
                + "    blib_entityNormal = vec4(blib_n * 0.5 + 0.5, 1.0);\n"
                + baseColorWrite
                + "    blib_entityDrawData = vec4(blib_detail, blib_lightCoord.x, blib_lightCoord.y, clamp(blib_faceLight, 0.0, 1.0));\n"
                + specularWrite
                + materialIdWrite;

        return insertBeforeFinalCloseBrace(withExt, writes);
    }

    /**
     * Minimal patch for sky/celestial/cloud/lightning shaders. Adds the six auxiliary outputs with explicit layout
     * locations and writes constant zero to each (except the mask byte, which carries the category) at the end of
     * {@code main()}. No varyings are declared (vertex stage is untouched) and no per-fragment data is computed —
     * the only goals are:
     * <ul>
     *   <li>Tag the fragment's category in the mask attachment so the post shader can route it correctly
     *       ({@code 0.0} for ambient sky, {@code 0.0625} for sun/moon textures).</li>
     *   <li>Guarantee deterministic zero in the rest of the auxiliaries instead of letting the GL spec's
     *       "undefined" value for unwritten outputs leak structured garbage (interpolated attribute remnants from
     *       the previous pipeline state) through the framebuffer — which is what shows up as green/blue ring
     *       banding in the sky and around the moon in thermal mode without this patch.</li>
     * </ul>
     * <p>
     * {@code discard} short-circuits {@code main()} before our writes run, but discarded fragments don't update any
     * attachment — the cleared zero is preserved, which is the same outcome we'd get from the explicit zero write.
     */
    private static String patchFragmentMinimal(String source, Category category) {
        var matcher = OUT_FRAG_COLOR.matcher(source);

        if (!matcher.find()) {
            return source;
        }

        var replacement =
            "layout(location = 0) out vec4 fragColor;\n"
                + "layout(location = 1) out vec4 blib_entityMask;\n"
                + "layout(location = 2) out vec4 blib_entityLightmap;\n"
                + "layout(location = 3) out vec4 blib_entityNormal;\n"
                + "layout(location = 4) out vec4 blib_entityDrawData;\n"
                + "layout(location = 5) out vec4 blib_entitySpecular;\n"
                + "layout(location = 6) out vec4 blib_entityMaterialId;\n";

        var withOuts = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
        var withExt = insertAfterVersion(withOuts, "#extension GL_ARB_explicit_attrib_location : require\n");

        var maskLiteral = String.format(java.util.Locale.ROOT, "%.4f", category.maskValue);

        // Celestial bodies (sun, moon) need to capture the raw texture luminance into entityDrawData.r so the
        // post shader can drive the recolor from the actual texture brightness rather than the post-blend
        // framebuffer luminance. MC draws sun/moon with additive blending (SRC_ALPHA, ONE) — at sun-quad pixels
        // where the texture itself is black (the corners and outer regions of vanilla sun.png), the framebuffer
        // color attachment ends up holding the underlying SKY color (since additive preserves dest), so the post
        // shader's `srcLuma` read at those pixels is sky-bright, not texture-dark. Without the raw-luma capture
        // here, the smoothstep-on-srcLuma curve can't tell "actually-dark sun corner" apart from "sky" and the
        // sun's rectangular quad bleeds through as a hard boundary against the surrounding ambient sky.
        //
        // Captured pre-modulation so the value reflects the texture itself rather than tinting from
        // {@code ColorModulator}. Additive blending on attachment 4 means the captured value at each pixel is
        // {@code SRC_ALPHA * texture_luma + dest} — for cleared sky pixels (dest=0) that's just
        // {@code SRC_ALPHA * texture_luma}, which is what we want.
        boolean isCelestial = category == Category.CELESTIAL;
        var canSampleBaseColor = isCelestial
            && SAMPLER0_DECL.matcher(source).find()
            && TEXCOORD0_DECL.matcher(source).find();
        String thermalDataWrite;

        if (canSampleBaseColor) {
            thermalDataWrite =
                "    vec3 blib_celestRGB = texture(Sampler0, texCoord0).rgb;\n"
                    + "    float blib_celestLuma = dot(blib_celestRGB, vec3(0.299, 0.587, 0.114));\n"
                    + "    blib_entityDrawData = vec4(clamp(blib_celestLuma, 0.0, 1.0), 0.0, 0.0, 1.0);\n";
        } else {
            thermalDataWrite = "    blib_entityDrawData = vec4(0.0, 0.0, 0.0, 1.0);\n";
        }

        var writes =
            "    blib_entityMask = vec4(" + maskLiteral + ", 0.0, 0.0, 1.0);\n"
                + "    blib_entityLightmap = vec4(0.0, 0.0, 0.0, 1.0);\n"
                + "    blib_entityNormal = vec4(0.5, 0.5, 0.5, 1.0);\n"
                + thermalDataWrite
                + "    blib_entitySpecular = vec4(0.0, 0.0, 0.0, 0.0);\n"
                + "    blib_entityMaterialId = vec4(0.0, 0.0, 0.0, 1.0);\n";

        return insertBeforeFinalCloseBrace(withExt, writes);
    }

    private static String insertAfterVersion(String source, String snippet) {
        var matcher = VERSION_DIRECTIVE.matcher(source);

        if (!matcher.find()) {
            // No #version → fall back to inserting at the very top.
            return snippet + source;
        }

        var insertAt = matcher.end();
        return source.substring(0, insertAt) + "\n" + snippet + source.substring(insertAt);
    }

    private static String insertBeforeMain(String source, String snippet) {
        var matcher = MAIN_OPEN.matcher(source);

        if (!matcher.find()) {
            return source;
        }

        var insertAt = matcher.start();
        return source.substring(0, insertAt) + snippet + source.substring(insertAt);
    }

    private static String insertAtMainStart(String source, String snippet) {
        var matcher = MAIN_OPEN.matcher(source);

        if (!matcher.find()) {
            return source;
        }

        var insertAt = matcher.end();
        return source.substring(0, insertAt) + "\n" + snippet + source.substring(insertAt);
    }

    private static String insertBeforeFinalCloseBrace(String source, String snippet) {
        var lastBrace = source.lastIndexOf('}');

        if (lastBrace < 0) {
            return source;
        }

        return source.substring(0, lastBrace) + snippet + source.substring(lastBrace);
    }
}
