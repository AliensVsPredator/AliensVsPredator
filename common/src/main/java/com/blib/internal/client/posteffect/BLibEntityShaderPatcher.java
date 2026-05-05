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
        PARTICLE(0.25F);

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

    private static final Pattern MAIN_OPEN = Pattern.compile("void\\s+main\\s*\\(\\s*\\)\\s*\\{");

    private static final Pattern VERSION_DIRECTIVE = Pattern.compile("(?m)^\\s*#version\\s+\\d+\\s*$");

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

        try {
            return switch (type) {
                case VERTEX -> patchVertex(source, category);
                case FRAGMENT -> patchFragment(source, category);
            };
        } catch (Exception e) {
            BLib.LOGGER.warn("[BLib] Failed to MRT-patch {} shader '{}': {}", type.getName(), shaderName, e.getMessage());
            return source;
        }
    }

    static @org.jetbrains.annotations.Nullable Category categoryFor(String name) {
        if (name.startsWith("rendertype_entity_")) {
            return (name.equals("rendertype_entity_shadow")
                || name.equals("rendertype_entity_glint")
                || name.equals("rendertype_entity_glint_direct"))
                    ? null
                    : Category.ENTITY;
        }

        if (name.startsWith("rendertype_armor_")) {
            return name.contains("glint") ? null : Category.ENTITY;
        }

        if (name.equals("rendertype_eyes")) {
            return Category.ENTITY;
        }

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

        // Mark the category in the patched vertex stage so the fragment can stamp its mask byte without re-querying
        // anything. (Currently unused on the vertex side because category drives a literal in the fragment, but kept
        // explicit for the same reason: future per-category vertex behavior is one branch away.)
        var categoryComment = "    // BLib MRT category: " + category.name() + "\n";

        var withDecls = insertBeforeMain(
            source,
            "out vec4 blib_lightmap;\nout vec3 blib_normal;\nout vec2 blib_lightCoord;\nout float blib_faceLight;\n"
        );
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

        var replacement =
            "in vec4 blib_lightmap;\n"
                + "in vec3 blib_normal;\n"
                + "in vec2 blib_lightCoord;\n"
                + "in float blib_faceLight;\n"
                + "layout(location = 0) out vec4 fragColor;\n"
                + "layout(location = 1) out float blib_entityMask;\n"
                + "layout(location = 2) out vec4 blib_entityLightmap;\n"
                + "layout(location = 3) out vec4 blib_entityNormal;\n"
                + "layout(location = 4) out vec4 blib_entityThermalData;\n"
                + "layout(location = 5) out vec4 blib_entitySpecular;\n"
                + "layout(location = 6) out float blib_entityMaterialId;\n";

        var withOuts = matcher.replaceFirst(Matcher.quoteReplacement(replacement));

        // `layout(location = ...)` is not part of GLSL 150 (vanilla entity shaders' version), so enable the
        // ARB extension that backports it. Insert immediately after the `#version` line.
        var withExt = insertAfterVersion(withOuts, "#extension GL_ARB_explicit_attrib_location : require\n");

        // Optional PBR/specular sampling. Vanilla shaders do not declare an EntitySpecular sampler, so this resolves
        // to a constant zero write — the thermal post shader's PBR branch then collapses to its non-PBR baseline. A
        // downstream mod that adds `uniform sampler2D EntitySpecular;` (and a parallel sampler binding on the render-
        // state side) will get the patcher to sample it through `texCoord0` automatically.
        var canSampleSpecular = ENTITY_SPECULAR_DECL.matcher(source).find()
            && TEXCOORD0_DECL.matcher(source).find();
        var specularWrite = canSampleSpecular
            ? "    blib_entitySpecular = texture(EntitySpecular, texCoord0);\n"
            : "    blib_entitySpecular = vec4(0.0);\n";

        // Optional material-ID hook (JCL ipbr_id equivalent). Vanilla shaders do not declare a BlibMaterialId uniform,
        // so the write defaults to 0 and the post shader sees a flat zero ID buffer. A downstream mod that injects the
        // uniform into a shader's source AND wires a per-draw setter (via the public ShaderInstance API) will get
        // their material IDs encoded at byte resolution into the entityMaterialId attachment automatically.
        var canSampleMaterialId = BLIB_MATERIAL_ID_DECL.matcher(source).find();
        var materialIdWrite = canSampleMaterialId
            ? "    blib_entityMaterialId = float(BlibMaterialId & 0xFF) / 255.0;\n"
            : "    blib_entityMaterialId = 0.0;\n";

        var maskLiteral = String.format(java.util.Locale.ROOT, "%.4f", category.maskValue);

        // `length() > 0.0` guards against the rare case where the interpolated normal collapses to zero.
        // entityThermalData channel layout:
        // R = detail = clamp(color.r, 0, 1)
        // G = raw normalized block-light coord (UV2.x / 240)
        // B = raw normalized sky-light coord (UV2.y / 240)
        // A = face-light from Normal vs Light0/Light1 (entities), synthetic key-light (terrain), or 1.0 (particles)
        // The "torch_color"-shaped weighted lightmap heat used in earlier revisions is trivially re-derived in the
        // post shader from `entityLightmap`, which is still a separate captured attachment.
        var writes =
            "    blib_entityMask = " + maskLiteral + ";\n"
                + "    blib_entityLightmap = blib_lightmap;\n"
                + "    vec3 blib_n = blib_normal;\n"
                + "    blib_n = length(blib_n) > 0.0 ? normalize(blib_n) : vec3(0.0, 1.0, 0.0);\n"
                + "    blib_entityNormal = vec4(blib_n * 0.5 + 0.5, 1.0);\n"
                + "    float blib_detail = clamp(color.r, 0.0, 1.0);\n"
                + "    blib_entityThermalData = vec4(blib_detail, blib_lightCoord.x, blib_lightCoord.y, clamp(blib_faceLight, 0.0, 1.0));\n"
                + specularWrite
                + materialIdWrite;

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
