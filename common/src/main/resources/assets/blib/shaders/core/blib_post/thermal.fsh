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

float luminance(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

float warmColorHeat(vec3 c) {
    float redDominance = max(c.r - max(c.g, c.b) * 0.6, 0.0);
    float warmAxis = max(c.r * 0.7 + c.g * 0.3 - c.b, 0.0);
    return clamp(redDominance * 1.6 + warmAxis * 0.6 + luminance(c) * 0.10 - 0.04, 0.0, 1.0);
}

// JCL's torch_color is the colored-lighting output, not the vanilla lightmap; without a port of that system the
// closest BLib has is the same channel-weighted lightmap sample used as the previous heat proxy.
vec3 torchColor(vec3 lightmapRGB) {
    return lightmapRGB;
}

float torchHeat(vec3 torch) {
    return min(1.0, torch.r * 0.2 + torch.g * 0.3 + torch.b * 0.5);
}

// Day = sunAngle in [0, 0.25] ∪ [0.75, 1.0]. Triangular ramp peaks at noon (sunAngle == 0) and at the wraparound
// (sunAngle == 1.0). Returns ~1 at noon, 0 at sunset/sunrise, 0 through the night.
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

vec3 thermalGradient(float t) {
    return t < 0.25 ? mix(vec3(0.0, 0.0, 0.0), vec3(0.0, 0.0, 1.0), t * 4.0)
        : t < 0.50 ? mix(vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 0.0), (t - 0.25) * 4.0)
        : t < 0.75 ? mix(vec3(0.0, 1.0, 0.0), vec3(1.0, 1.0, 0.0), (t - 0.50) * 4.0)
        : t < 1.00 ? mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), (t - 0.75) * 4.0)
        : mix(vec3(1.0, 0.0, 0.0), vec3(1.0), (t - 1.0) * 4.0);
}

void main() {
    vec4 src = texture(DiffuseSampler, texCoord);
    float mask = texture(entityMask, texCoord).r;
    vec3 entityLight = texture(entityLightmap, texCoord).rgb;
    vec4 entityThermal = texture(entityThermalData, texCoord);
    vec4 specular = texture(entitySpecular, texCoord);
    // Material ID byte (0..255) packed into [0, 1]. Mirrors JCL's `ipbr_id` lookup. Until a downstream mod populates
    // the BlibMaterialId uniform this is always zero, and `materialBoost` below collapses to 0.
    int materialId = int(round(texture(entityMaterialId, texCoord).r * 255.0));

    // Category breakdown of the mask byte. Values written by BLibEntityShaderPatcher.Category.
    //   1.00 = entity         → full body-heat formula
    //   0.50 = terrain        → captured-data world heat (per-face/per-light), no body heat
    //   0.25 = particle       → captured lightmap heat (no face), no body heat
    //   0.00 = unpatched/sky  → warm-color heuristic over src.rgb
    float catEntity   = step(0.75, mask);
    float catTerrain  = step(0.375, mask) - catEntity;
    float catParticle = step(0.125, mask) - step(0.375, mask);
    float catWorld    = 1.0 - step(0.125, mask);

    // Use captured per-pixel detail for any patched fragment; fall back to src.r for unpatched pixels.
    float patchedDetail = entityThermal.r;
    float drawDetail = mix(src.r, patchedDetail, 1.0 - catWorld);
    float blockLight = entityThermal.g;
    float skyLight = entityThermal.b;
    float faceLight = entityThermal.a;

    vec3 torch = torchColor(entityLight);
    float captureTorchHeat = torchHeat(torch);

    float worldHeat = max(
        torchHeat(src.rgb),
        warmColorHeat(src.rgb)
    );

    float sunFactor = sunShadowApprox(skyLight, faceLight, sunAngle);

    // JCL non-PBR branch (entities):
    //   heat = torch_color weighted + 0.5 (body heat) + day sky contribution - detail * 0.3.
    float entityHeatNonPBR =
        captureTorchHeat
        + 0.5
        + sunFactor * (0.5 + 0.3 * skyLight)
        + 0.15 * blockLight;

    // JCL PBR branch (NIGHT_VISION_MODE == 2 PBR):
    //   heat = min(1, specular.a + torch_color.r*0.2 + torch_color.g*0.3 + torch_color.b*0.5)
    //          * (1 + specular.g * (torch_color.r - 0.5));
    //   heat += 0.5 - 0.5 * specular.g;
    // When specular is all zero (the default for vanilla entity rendering) this collapses to:
    //   heat = captureTorchHeat + 0.5 — i.e. the non-PBR base — so the PBR formula is safe to mix.
    float entityHeatPBR =
        min(1.0, specular.a + captureTorchHeat) * (1.0 + specular.g * (torch.r - 0.5))
        + (0.5 - 0.5 * specular.g);

    float pbrWeight = clamp(specular.a + specular.g, 0.0, 1.0);
    float entityHeat = mix(entityHeatNonPBR, entityHeatPBR, pbrWeight);

    // Terrain: use the captured per-fragment lighting (block light, sun factor) without body heat. Includes
    // warm-color emissive heuristic so lava/magma/fire still read warm via src.rgb even though terrain blocks
    // don't write a non-zero body-heat baseline.
    float terrainHeat =
        captureTorchHeat
        + 0.20 * blockLight
        + 0.30 * sunFactor * skyLight
        + warmColorHeat(src.rgb) * 0.6;

    // Particles: usually emissive smoke/spark/flame; surface them as warm using the captured lightmap luminance and
    // the diffuse color heuristic — but no body-heat baseline so non-emissive particles don't create false reads.
    float particleHeat =
        captureTorchHeat
        + warmColorHeat(src.rgb) * 0.7;

    // JCL-style per-material heat additions. Mirrors patterns like `abs(ipbr_id - 10032.) < .5` from JCL's PBR
    // branch, but BLib uses single-byte IDs so the sentinel space is 0..255 instead of JCL's 5-digit IDs. The
    // mappings below are intentional defaults a downstream mod can reinterpret by choosing matching IDs:
    //   1 = lava-like        (+0.45)
    //   2 = magma-like       (+0.30)
    //   3 = fire/torch-flame (+0.55)
    //   4 = redstone-active  (+0.20)
    //   5 = warm-blooded     (+0.25 on top of body heat)
    // ID 0 (the vanilla default) contributes 0.
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
        + catWorld    * worldHeat
        + materialBoost;
    heat = max(heat - drawDetail * 0.3, 0.0);

    vec3 heatVis = thermalGradient(heat);

    // Coverage strength. JCL's NIGHT_VISION_MODE == 2 mixes thermal over the world via
    //   mix(src, heat_vis, max(nightVision, nv_effect))
    // where nv_effect is a radial center-vignette and nightVision is the potion strength. Without the potion that
    // formula leaves the screen edges showing the unmodified world — surprising for a "thermal vision toggle on."
    // BLib's thermal post-effect runs only when explicitly enabled, so the user always wants full-screen coverage;
    // we therefore drop the radial vignette and force the mix to 1 (i.e. always show heat). The outSize and
    // nightVision uniforms remain declared for forward-compat with downstream effects that want JCL's exact blend.
    vec3 outColor = heatVis;
    outColor *= 1.0 - max(blindness, darkness) * BLIB_FOG_AMOUNT;

    fragColor = vec4(outColor, 1.0);
}
