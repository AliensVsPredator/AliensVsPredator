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

// World-space directions for the sun and moon, derived in BLibPostEffectStdUniforms from MC's renderSky transform
// stack. Both unit length: dot(viewRay, sunDir) is cos(angle from sun) and gives a clean sun-vs-moon disambiguation
// at celestial-body pixels regardless of where the body is on screen.
uniform vec3 sunDir;
uniform vec3 moonDir;

// Captured at the top of the level pass (NOT RenderSystem.getProjectionMatrix() at post time, which is the
// orthographic GUI projection — that would produce ortho-frustum rays instead of perspective rays and the
// sun-vs-moon ray test would fail for off-axis pixels.
uniform mat4 invProjMat;
uniform mat4 gbufferModelViewInverse;

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

// Reconstructs the world-space view ray for a screen-space pixel by pushing it to the far plane in NDC, inverting
// the projection to view space, then inverse-viewing to world space. Returns a unit vector from the camera through
// the pixel. Used only for sky/celestial pixels — for non-sky pixels the actual depth would matter and we'd need
// the depth texture.
vec3 reconstructSkyRay(vec2 uv) {
    vec4 ndc = vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    vec4 viewH = invProjMat * ndc;
    vec3 viewDir = viewH.xyz / viewH.w;
    vec3 worldDir = (gbufferModelViewInverse * vec4(viewDir, 0.0)).xyz;
    return normalize(worldDir);
}

// Stylized procedural thermal sky.
//
// Two regions, distinguished by the celestial-luma channel captured into entityThermalData.r by the patcher:
//   - Ambient sky (drawDetail ≈ 0 — no celestial body drawn here): blue at night → green at day, smooth dawn/dusk.
//   - Celestial body (drawDetail = SRC_ALPHA * texture_luma): the sun/moon texture, recolored via the heat gradient
//     so the texture's BRIGHTNESS drives the heat. Bright sun core → white-hot; bright moon phase → yellow; dim
//     halo regions of the texture → fade smoothly back to the ambient sky baseline.
//
// Held items are bypassed earlier in main() and rendered as their original color.
//
// Why drawDetail instead of the framebuffer's source luminance? MC draws sun/moon with ADDITIVE blending
// (SRC_ALPHA, ONE) — at sun-quad pixels where the texture itself is black (the corners and outer regions of
// vanilla sun.png), the framebuffer color attachment ends up holding the underlying SKY color (since additive
// preserves dest), so a `srcLuma` read at those pixels returns the sky luminance, not the texture darkness. That
// would push the recolor above baseHeat at every sun-quad corner and produce a hard rectangular boundary against
// the surrounding sky. The patcher captures the raw texture luma into entityThermalData.r BEFORE the modulator
// multiply, so drawDetail is genuinely 0 at black-texture pixels regardless of what the framebuffer holds — and
// the corners then read as baseHeat exactly, indistinguishable from the surrounding sky.
float computeSkyHeat(vec2 uv, float drawDetail) {
    // Time-of-day factor from sun world-space height. 1 at noon, 0 at midnight, smooth transition while the sun
    // is within ±~9° of the horizon (wider thresholds → longer twilight).
    float dayFactor = smoothstep(-0.15, 0.15, sunDir.y);

    // Ambient sky baseline: blue (heat 0.25) at night, green (heat 0.5) at day. Lerping through heat gives a clean
    // dawn/dusk pass through the gradient's natural blue→green transition rather than fading colors directly.
    float baseHeat = mix(0.25, 0.5, dayFactor);

    // Sun vs moon disambiguation: the celestial body in front of the camera is whichever direction the view ray
    // is more aligned with. cosSun > cosMoon → looking at sun's hemisphere, otherwise moon. Cheap and bullet-proof
    // since sunDir = -moonDir (always exactly opposite hemispheres in MC's celestial sphere).
    vec3 ray = reconstructSkyRay(uv);
    float cosSun = dot(ray, sunDir);
    float cosMoon = dot(ray, moonDir);
    float celestialTarget = (cosSun > cosMoon) ? 1.25 : 0.75;

    // Direct texture-brightness → heat mapping. Linear so the texture's natural luma falloff drives the radial
    // gradient across the body. drawDetail is exactly 0 at non-celestial sky pixels (cleared) and at black
    // texture pixels (corners), so heat = baseHeat at both — no rectangular boundary against the ambient sky.
    return mix(baseHeat, celestialTarget, clamp(drawDetail, 0.0, 1.0));
}

void main() {
    vec3 src = texture(DiffuseSampler, texCoord).rgb;
    float mask = texture(entityMask, texCoord).r;
    vec4 entityThermal = texture(entityThermalData, texCoord);
    vec4 specular = texture(entitySpecular, texCoord);
    int materialId = int(round(texture(entityMaterialId, texCoord).r * 255.0));

    // Category breakdown of the mask byte. Values written by BLibEntityShaderPatcher.Category, plus the special
    // 0.875 "held-item passthrough" written when BlibHeldItem is set during first-person hand or third-person
    // ItemInHandLayer draws. We detect that range FIRST and short-circuit to original color so held items remain
    // readable as items in thermal mode.
    //   0.875  = held item     → outColor = src.rgb (no thermal recoloring)
    //   1.000  = entity        → body heat + lighting
    //   0.500  = terrain       → lighting only (no body heat)
    //   0.250  = particle      → ambient block light only
    //   0.0625 = celestial     → sun/moon texture recolored via heat gradient + ambient-sky fade
    //   0.000  = sky/passthrough → ambient sky baseline only
    bool isHeldItem = mask > 0.8125 && mask < 0.9375;

    if (isHeldItem) {
        vec3 passthru = src;
        passthru *= 1.0 - max(blindness, darkness) * BLIB_FOG_AMOUNT;
        fragColor = vec4(passthru, 1.0);
        return;
    }

    float catEntity   = step(0.75, mask);
    float catTerrain  = step(0.375, mask) - catEntity;
    float catParticle = step(0.125, mask) - step(0.375, mask);
    // Sky/celestial — everything below the particle threshold. Includes both pure sky (mask=0) and celestial
    // bodies (mask ≈ 0.0625). The two are distinguished inside computeSkyHeat by drawDetail (entityThermal.r):
    // 0 for pure sky, non-zero for celestial-body texture pixels.
    float catSky      = 1.0 - step(0.125, mask);

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

    // Procedural sky+celestial heat. Cheap to compute even on non-sky pixels; gated by catSky below.
    // drawDetail (= entityThermal.r) is the celestial-body texture luminance captured by the patcher's CELESTIAL
    // path; 0 at non-celestial sky pixels because the cleared auxiliary buffer is 0 and the passthrough shaders
    // explicitly write 0 there.
    float skyHeat = computeSkyHeat(texCoord, drawDetail);

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
        + catSky      * skyHeat
        + materialBoost;

    // Detail subtraction adds texture-level variation but at full strength it cancels out the heat of bright
    // emissive textures. Fade detail subtraction out as emission rises, AND skip it for sky pixels — the sky
    // procedural path already encodes texture detail via srcLuma; subtracting drawDetail from it would distort
    // the sun's bright core back toward base, defeating the purpose.
    float detailWeight = 0.30 * (1.0 - emission) * (1.0 - catSky);
    heat = max(heat - drawDetail * detailWeight, 0.0);

    vec3 heatVis = thermalGradient(heat);

    // Cold-area visibility underlay — only for non-sky pixels. At low heat the gradient color is a near-uniform
    // dark blue, which obliterates structure (walls/floor/edges) and makes navigation hard. Add a pure-blue underlay
    // scaled by source luminance so block edges and lit-side faces brighten the blue. Sky pixels are excluded
    // (catSky multiplier) — their heat already comes from a clean procedural source, and adding a luma-scaled blue
    // on top would re-introduce source-color leak through the sky region (the green ring around unloaded chunks,
    // faint banding from celestial-body texture luminance, etc.).
    float srcLuma = dot(src, vec3(0.299, 0.587, 0.114));
    float liftedLuma = pow(clamp(srcLuma, 0.0, 1.0), 0.5);
    float coldFade = (1.0 - smoothstep(0.0, 0.5, heat)) * (1.0 - catSky);
    vec3 coldDetail = vec3(0.0, 0.0, 1.0) * liftedLuma * 0.5 * coldFade;

    vec3 outColor = heatVis + coldDetail;
    outColor *= 1.0 - max(blindness, darkness) * BLIB_FOG_AMOUNT;

    fragColor = vec4(outColor, 1.0);
}
