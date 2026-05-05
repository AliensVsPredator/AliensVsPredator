package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.shaders.Program;
import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blib.mod.BLib;

/**
 * Source-string transformer that injects MRT auxiliary outputs into vanilla entity-family shaders so the rest of the
 * post-effect framework can read per-fragment "this is an entity" + "lightmap value at the entity" without a re-render
 * pass.
 * <p>
 * Vertex side: declares a {@code blib_lightmap} varying and writes {@code texelFetch(Sampler2, UV2 / 16, 0)} to it (or
 * {@code vec4(0)} for the few entity shaders that lack a {@code UV2} attribute). Fragment side: turns the lone
 * {@code out vec4 fragColor;} declaration into three explicitly-located outputs (color, mask, lightmap) and writes
 * {@code 1.0} to the mask + the varying value to the lightmap output at the end of {@code main()}.
 * <p>
 * If the source doesn't match the expected anchors (e.g., Mojang restructures shaders in a future version), the patcher
 * returns the source unchanged and logs a warning rather than producing non-compilable GLSL.
 * <p>
 * Bypassed entirely when {@link BLibIrisCompat#isShaderModActive()} — Iris owns the entity shaders in that case.
 */
@ApiStatus.Internal
public final class BLibEntityShaderPatcher {

    private static final Pattern OUT_FRAG_COLOR = Pattern.compile("(?m)^\\s*out\\s+vec4\\s+fragColor\\s*;\\s*$");

    private static final Pattern UV2_DECL = Pattern.compile("(?m)^\\s*in\\s+ivec2\\s+UV2\\s*;\\s*$");

    private static final Pattern SAMPLER2_DECL = Pattern.compile("(?m)^\\s*uniform\\s+sampler2D\\s+Sampler2\\s*;\\s*$");

    private static final Pattern MAIN_OPEN = Pattern.compile("void\\s+main\\s*\\(\\s*\\)\\s*\\{");

    private static final Pattern VERSION_DIRECTIVE = Pattern.compile("(?m)^\\s*#version\\s+\\d+\\s*$");

    private BLibEntityShaderPatcher() {
        throw new UnsupportedOperationException();
    }

    public static String transform(String shaderName, Program.Type type, String source) {
        if (BLibIrisCompat.isShaderModActive()) {
            return source;
        }

        if (!isEntityShader(shaderName)) {
            return source;
        }

        try {
            return switch (type) {
                case VERTEX -> patchVertex(source);
                case FRAGMENT -> patchFragment(source);
            };
        } catch (Exception e) {
            BLib.LOGGER.warn("[BLib] Failed to MRT-patch {} shader '{}': {}", type.getName(), shaderName, e.getMessage());
            return source;
        }
    }

    static boolean isEntityShader(String name) {
        if (name.startsWith("rendertype_entity_")) {
            return !name.equals("rendertype_entity_shadow")
                && !name.equals("rendertype_entity_glint")
                && !name.equals("rendertype_entity_glint_direct");
        }

        if (name.startsWith("rendertype_armor_")) {
            return !name.contains("glint");
        }

        return name.equals("rendertype_eyes");
    }

    private static String patchVertex(String source) {
        var canSampleLightmap = UV2_DECL.matcher(source).find() && SAMPLER2_DECL.matcher(source).find();

        var write = canSampleLightmap
            ? "    blib_lightmap = texelFetch(Sampler2, UV2 / 16, 0);\n"
            : "    blib_lightmap = vec4(0.0);\n";

        var withDecl = insertBeforeMain(source, "out vec4 blib_lightmap;\n");
        return insertAtMainStart(withDecl, write);
    }

    private static String patchFragment(String source) {
        var matcher = OUT_FRAG_COLOR.matcher(source);

        if (!matcher.find()) {
            return source;
        }

        var replacement =
            "in vec4 blib_lightmap;\n"
                + "layout(location = 0) out vec4 fragColor;\n"
                + "layout(location = 1) out float blib_entityMask;\n"
                + "layout(location = 2) out vec4 blib_entityLightmap;\n";

        var withOuts = matcher.replaceFirst(Matcher.quoteReplacement(replacement));

        // `layout(location = ...)` is not part of GLSL 150 (vanilla entity shaders' version), so enable the
        // ARB extension that backports it. Insert immediately after the `#version` line.
        var withExt = insertAfterVersion(withOuts, "#extension GL_ARB_explicit_attrib_location : require\n");

        var writes =
            "    blib_entityMask = 1.0;\n"
                + "    blib_entityLightmap = blib_lightmap;\n";

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
