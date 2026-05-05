#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D entityMask;
uniform sampler2D entityLightmap;
uniform sampler2D entityThermalData;
uniform sampler2D entitySpecular;
uniform sampler2D entityMaterialId;

uniform float sunAngle;
uniform float blindness;
uniform float darkness;
uniform float nightVision;
uniform vec2 outSize;
uniform float ultrawarmAmbient;

in vec2 texCoord;

out vec4 fragColor;

// JCL's `fog_amount` is a shaderpack-internal term BLib doesn't capture. Default to 1.0 so blindness/darkness still
// dim correctly; can be replaced with a captured value if/when the broader render-pipeline data is wired in.
const float BLIB_FOG_AMOUNT = 1.0;

// Cold end is a very dark blue rather than pure black so unlit areas still read as "ambient cold" — pure black
// looks like missing data / GUI clear and breaks immersion in fully-dark caves.
const vec3 BLIB_THERMAL_COLD = vec3(0.0, 0.0, 0.06);

// Hot end: mostly white with a faint hint of red retained from the previous gradient stop, instead of pure
// (1, 1, 1) — pure white reads as eye-searing on common monitors when a full lava block fills the over-1.0 range.
// Mix factor is clamped (clamp(..., 0, 1)) so heat values >> 1.0 don't extrapolate past this color into negative
// red territory (which would clamp to high green+blue = cyan).
const vec3 BLIB_THERMAL_HOT = vec3(1.0, 0.92, 0.92);

vec3 thermalGradient(float t) {
    return t < 0.25 ? mix(BLIB_THERMAL_COLD, vec3(0.0, 0.0, 1.0), t * 4.0)
        : t < 0.50 ? mix(vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 0.0), (t - 0.25) * 4.0)
        : t < 0.75 ? mix(vec3(0.0, 1.0, 0.0), vec3(1.0, 1.0, 0.0), (t - 0.50) * 4.0)
        : t < 1.00 ? mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), (t - 0.75) * 4.0)
        : mix(vec3(1.0, 0.0, 0.0), BLIB_THERMAL_HOT, clamp((t - 1.0) * 4.0, 0.0, 1.0));
}

void main() {
    vec3 src = texture(DiffuseSampler, texCoord).rgb;
    float mask = texture(entityMask, texCoord).r;
    vec4 entityThermal = texture(entityThermalData, texCoord);
    vec4 specular = texture(entitySpecular, texCoord);
    int materialId = int(round(texture(entityMaterialId, texCoord).r * 255.0));

    // Category breakdown of the mask byte. Values written by BLibEntityShaderPatcher.Category.
    //   1.00 = entity         → body heat + lighting
    //   0.50 = terrain        → lighting only (no body heat)
    //   0.25 = particle       → ambient block light only
    //   0.00 = unpatched/sky  → cold (no thermal information)
    float catEntity   = step(0.75, mask);
    float catTerrain  = step(0.375, mask) - catEntity;
    float catParticle = step(0.125, mask) - step(0.375, mask);

    // entityThermalData payload — all biome/dimension-independent:
    //   R = untinted Sampler0.r (texture detail, no lightmap tint)
    //   G = block-light coord — for entities, *per-bone* via the BLibPerBoneLight mixin (each bone's packedLight
    //       is sampled at the bone's actual world position, so different bones of the same mob can read different
    //       block-light values when the mob spans a lighting boundary). For terrain/particles this is the standard
    //       per-vertex UV2.x.
    //   B = sky-light coord — captured but intentionally unused in heat formulas (kept for the debug visualization
    //       at mode 6). Sun-warmed surfaces aren't measurably hotter in IR vision; including sky light made noon
    //       fields read as warm green/yellow which doesn't match thermal-vision expectations.
    //   A = face-light (Light0/Light1 dot for entities, synthetic key-light for terrain, 1.0 for particles)
    float drawDetail = entityThermal.r;
    // Block-light is floored by the dimension's ultrawarm ambient (≈ 0.467 in the Nether, 0 elsewhere). The Nether
    // is officially "ultra-warm" so even fully-occluded fragments register as green-ambient hot — caves still cold
    // in the overworld, but in the Nether everything reads warm.
    float blockLight = max(entityThermal.g, ultrawarmAmbient);
    float faceLight = entityThermal.a;

    // Emission term — biome-independent. specular.a holds either:
    //   (a) A LabPBR emission sample if the entity shader declared an EntitySpecular sampler, or
    //   (b) The warm-color heuristic the patcher precomputed when no PBR sampler was bound.
    // The smoothstep gate by raw block light suppresses the heuristic on warm-colored *non-emissive* fragments.
    float emission = specular.a * smoothstep(0.86, 1.0, blockLight);

    // Heat is driven entirely by block light (and emission for true heat sources). Sky light + sun terms removed:
    // sunlit terrain isn't really hotter than shaded terrain in IR, and including sun heat made daytime outdoor
    // scenes wash out as warm everywhere.
    float entityHeatNonPBR =
        0.5
        + 0.40 * blockLight
        + 0.60 * emission;

    float entityHeatPBR =
        min(1.0, emission + blockLight) * (1.0 + specular.g * (blockLight - 0.5))
        + (0.5 - 0.5 * specular.g);

    // Only formulate as PBR when real LabPBR roughness is present.
    float pbrWeight = clamp(specular.g, 0.0, 1.0);
    float entityHeat = mix(entityHeatNonPBR, entityHeatPBR, pbrWeight);

    // Terrain: block light drives heat across the full gradient range so torches/lava produce smooth radiance falloff.
    float terrainHeat = blockLight + 1.50 * emission;

    float particleHeat = blockLight + 0.80 * emission;

    // JCL-style per-material heat additions.
    float materialBoost =
        (materialId == 1 ? 0.45 : 0.0)
        + (materialId == 2 ? 0.30 : 0.0)
        + (materialId == 3 ? 0.55 : 0.0)
        + (materialId == 4 ? 0.20 : 0.0)
        + (materialId == 5 ? 0.25 : 0.0);

    float heat =
        catEntity   * entityHeat
        + catTerrain  * terrainHeat
        + catParticle * particleHeat
        + materialBoost;

    // Detail subtraction adds texture-level variation but at full strength it cancels out the heat of bright
    // emissive textures. Fade detail subtraction out as emission rises.
    float detailWeight = 0.30 * (1.0 - emission);
    heat = max(heat - drawDetail * detailWeight, 0.0);

    vec3 heatVis = thermalGradient(heat);

    // Cold-area visibility underlay. At low heat the gradient color is a near-uniform dark blue, which obliterates
    // structure (walls/floor/edges) and makes navigation hard. Add a pure-blue underlay scaled by source luminance
    // so block edges and lit-side faces brighten the blue. The tint is pure blue (gradient's heat=0.25 endpoint)
    // so cold areas stay in the cold-blue palette instead of greying out. Luminance-only sample keeps biome tint
    // out. Fades to zero by heat=0.5 so the warm half of the gradient stays pure.
    float srcLuma = dot(src, vec3(0.299, 0.587, 0.114));
    float liftedLuma = pow(clamp(srcLuma, 0.0, 1.0), 0.5);
    float coldFade = 1.0 - smoothstep(0.0, 0.5, heat);
    vec3 coldDetail = vec3(0.0, 0.0, 1.0) * liftedLuma * 0.5 * coldFade;

    vec3 outColor = heatVis + coldDetail;
    outColor *= 1.0 - max(blindness, darkness) * BLIB_FOG_AMOUNT;

    fragColor = vec4(outColor, 1.0);
}
