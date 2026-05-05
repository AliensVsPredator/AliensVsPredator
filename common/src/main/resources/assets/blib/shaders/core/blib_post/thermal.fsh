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

in vec2 texCoord;

out vec4 fragColor;

// JCL's `fog_amount` is a shaderpack-internal term BLib doesn't capture. Default to 1.0 so blindness/darkness still
// dim correctly; can be replaced with a captured value if/when the broader render-pipeline data is wired in.
const float BLIB_FOG_AMOUNT = 1.0;

// Day = sunAngle in [0, 0.25] ∪ [0.75, 1.0]. Triangular ramp peaks at noon (sunAngle == 0) and at the wraparound
// (sunAngle == 1.0). Returns ~1 at noon, 0 at sunset/sunrise, 0 through the night. The Nether/End report
// constant non-day sunAngle, so this naturally returns 0 there — no false sun heat in dimensions without a sun.
float dayFactor(float angle) {
    float dist = min(angle, 1.0 - angle);
    return clamp(1.0 - dist * 4.0, 0.0, 1.0);
}

// Approximation of JCL's shadow/sun term without an actual shadow map. Combines:
//   - Day strength (only sun heat during day).
//   - Sky-light coord (occluded fragments with low sky light read as in shadow).
//   - Face light (faces pointing toward the vanilla key light catch more sun).
// Returns the effective sun contribution multiplier in [0, 1].
float sunShadowApprox(float skyLight, float faceLight, float angle) {
    float day = dayFactor(angle);
    float skyMix = smoothstep(0.4, 0.95, skyLight);
    float faceMix = mix(0.4, 1.0, clamp(faceLight, 0.0, 1.0));
    return day * skyMix * faceMix;
}

// Cold end is a very dark blue rather than pure black so unlit areas still read as "ambient cold" — pure black
// looks like missing data / GUI clear and breaks immersion in fully-dark caves.
const vec3 BLIB_THERMAL_COLD = vec3(0.0, 0.0, 0.06);

vec3 thermalGradient(float t) {
    return t < 0.25 ? mix(BLIB_THERMAL_COLD, vec3(0.0, 0.0, 1.0), t * 4.0)
        : t < 0.50 ? mix(vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 0.0), (t - 0.25) * 4.0)
        : t < 0.75 ? mix(vec3(0.0, 1.0, 0.0), vec3(1.0, 1.0, 0.0), (t - 0.50) * 4.0)
        : t < 1.00 ? mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), (t - 0.75) * 4.0)
        : mix(vec3(1.0, 0.0, 0.0), vec3(1.0), (t - 1.0) * 4.0);
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
    //   G = raw block-light coord
    //   B = raw sky-light coord
    //   A = face-light (Light0/Light1 dot for entities, synthetic key-light for terrain, 1.0 for particles)
    float drawDetail = entityThermal.r;
    float blockLight = entityThermal.g;
    float skyLight = entityThermal.b;
    float faceLight = entityThermal.a;

    float sunFactor = sunShadowApprox(skyLight, faceLight, sunAngle);

    // Emission term — biome-independent. specular.a holds either:
    //   (a) A LabPBR emission sample if the entity shader declared an EntitySpecular sampler, or
    //   (b) The warm-color heuristic (specular.a = warmColorHeat(Sampler0.rgb)) the patcher precomputed when no PBR
    //       sampler was bound — captures dominantly-red textures like lava/fire/magma/redstone.
    // The smoothstep gate by raw block light suppresses the heuristic on warm-colored *non-emissive* fragments.
    // Lava emits block-light 15 (BL ≈ 1.0); torches emit 14 (BL ≈ 0.93); both are inside the [0.86, 1.0] window so
    // they read emissive. Red wool away from a torch sits at low BL → emission collapses to 0.
    float emission = specular.a * smoothstep(0.86, 1.0, blockLight);

    // JCL non-PBR branch (entities), driven by raw light coords only — no biome-tinted sampled lightmap RGB and no
    // scene-color heuristics, so the result is identical between e.g. Nether Wastes and Soul Sand Valley for the
    // same lighting/face/body conditions.
    //   heat = body 0.5 + ambient block-light contribution + day sky contribution + emission boost
    float entityHeatNonPBR =
        0.5
        + 0.40 * blockLight
        + sunFactor * (0.5 + 0.3 * skyLight)
        + 0.60 * emission;

    // JCL PBR branch (NIGHT_VISION_MODE == 2 PBR). JCL's `torch_color.r` was a colored-lighting red value centered
    // around ~0.5; with no colored-lighting port we substitute raw block-light coord (also centered around 0.5 at
    // medium torchlight). The PBR formula already pulls emission from specular.a, so the gating done above into
    // `emission` is also the right form for the multiplier here.
    float entityHeatPBR =
        min(1.0, emission + blockLight) * (1.0 + specular.g * (blockLight - 0.5))
        + (0.5 - 0.5 * specular.g);

    // Only formulate as PBR when real LabPBR roughness is present. The fallback warm-heuristic populates specular.a
    // alone, so weighting on specular.g keeps non-PBR draws on the simpler non-PBR formula.
    float pbrWeight = clamp(specular.g, 0.0, 1.0);
    float entityHeat = mix(entityHeatNonPBR, entityHeatPBR, pbrWeight);

    // Terrain: block light drives heat across the full gradient range (1.0 coefficient) so the smooth MC light
    // falloff around a torch/lava/fire produces a smooth thermal falloff: BL≈1 at the source → red, BL≈0.86 at
    // distance 2 → orange, BL≈0.5 mid-range → yellow-green, BL≈0.2 far → blue. Sky light contributes much less
    // (sunlit fields aren't really "hot" in IR vision); emission saturates true heat sources to white.
    float terrainHeat =
        blockLight
        + 0.30 * sunFactor * skyLight
        + 1.50 * emission;

    // Particles: same block-light driven curve so smoke/sparks lit by surrounding light fade gradually. Fire/spark
    // particles also pick up the emission boost from their warm-color textures + max block light.
    float particleHeat = blockLight + 0.80 * emission;

    // JCL-style per-material heat additions. Mirrors patterns like `abs(ipbr_id - 10032.) < .5` from JCL's PBR
    // branch, but BLib uses single-byte IDs so the sentinel space is 0..255 instead of JCL's 5-digit IDs. ID 0
    // (the vanilla default) contributes 0; downstream mods can populate BlibMaterialId per-draw to surface
    // dimension-independent thermal hot spots regardless of biome tint.
    //   1 = lava-like        (+0.45)
    //   2 = magma-like       (+0.30)
    //   3 = fire/torch-flame (+0.55)
    //   4 = redstone-active  (+0.20)
    //   5 = warm-blooded     (+0.25 on top of body heat)
    float materialBoost =
        (materialId == 1 ? 0.45 : 0.0)
        + (materialId == 2 ? 0.30 : 0.0)
        + (materialId == 3 ? 0.55 : 0.0)
        + (materialId == 4 ? 0.20 : 0.0)
        + (materialId == 5 ? 0.25 : 0.0);

    // Uncaptured fragments (sky, GUI, anything outside the patched render-type families) read as cold — there's no
    // per-fragment lighting data to derive heat from. Sky reads black through thermal, which is correct for an
    // infrared-style view.
    float heat =
        catEntity   * entityHeat
        + catTerrain  * terrainHeat
        + catParticle * particleHeat
        + materialBoost;

    // Detail subtraction adds texture-level variation but at full strength it cancels out the heat of bright
    // emissive textures (lava's red is exactly what the warmth heuristic just used to call it hot — subtracting
    // 0.3 * red there would push lava back toward blue). Fade detail subtraction out as emission rises.
    float detailWeight = 0.30 * (1.0 - emission);
    heat = max(heat - drawDetail * detailWeight, 0.0);

    vec3 heatVis = thermalGradient(heat);

    // Cold-area visibility underlay. At low heat the gradient color is a near-uniform dark blue, which obliterates
    // structure (walls/floor/edges) and makes navigation hard. We brighten the cold-blue value based on the source
    // scene's luminance so block edges and lit-side faces catch more brightness — but the contribution is tinted
    // to pure blue (matching the gradient's heat=0.25 endpoint), so the cold area stays in the blue palette
    // instead of greying out. Luminance (not RGB) is sampled to keep biome tint out. Fades to zero by heat=0.5
    // so the warm half of the gradient stays pure.
    float srcLuma = dot(src, vec3(0.299, 0.587, 0.114));
    float liftedLuma = pow(clamp(srcLuma, 0.0, 1.0), 0.5);
    float coldFade = 1.0 - smoothstep(0.0, 0.5, heat);
    vec3 coldDetail = vec3(0.0, 0.0, 1.0) * liftedLuma * 0.5 * coldFade;

    // BLib's thermal post-effect runs only when explicitly enabled, so the user always wants full-screen coverage.
    // The radial nv_effect / nightVision blend from JCL's NIGHT_VISION_MODE == 2 is intentionally dropped here;
    // outSize and nightVision remain declared for forward-compat with downstream effects that want that blend.
    vec3 outColor = heatVis + coldDetail;
    outColor *= 1.0 - max(blindness, darkness) * BLIB_FOG_AMOUNT;

    fragColor = vec4(outColor, 1.0);
}
