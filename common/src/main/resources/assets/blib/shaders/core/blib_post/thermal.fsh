#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D depthtex0;
uniform sampler2D lightmap;
uniform sampler2D entityMask;
uniform sampler2D entityLightmap;

uniform float time;
uniform float partialTick;
uniform vec2 outSize;
uniform vec3 cameraPos;
uniform float sunAngle;
uniform float nightVision;
uniform float blindness;
uniform float darkness;

in vec2 texCoord;

out vec4 fragColor;

float luminance(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

// Heuristic warmth from a fragment's color: lots of red without much blue → warm; bright in general → warmer.
// Catches torches, lava, fire, glowstone-lit terrain, sun-lit faces, etc. without needing per-fragment lightmap.
float worldHeatFromColor(vec3 c) {
    float redDominance = max(c.r - max(c.g, c.b) * 0.6, 0.0);
    float warmAxis = max(c.r * 0.7 + c.g * 0.3 - c.b, 0.0);
    return clamp(redDominance * 1.6 + warmAxis * 0.6 + luminance(c) * 0.10 - 0.04, 0.0, 1.0);
}

vec3 thermalGradient(float t) {
    if (t < 0.20) {
        return mix(vec3(0.00, 0.00, 0.02), vec3(0.05, 0.10, 0.45), t / 0.20);
    } else if (t < 0.40) {
        return mix(vec3(0.05, 0.10, 0.45), vec3(0.00, 0.55, 0.35), (t - 0.20) / 0.20);
    } else if (t < 0.60) {
        return mix(vec3(0.00, 0.55, 0.35), vec3(1.00, 1.00, 0.10), (t - 0.40) / 0.20);
    } else if (t < 0.85) {
        return mix(vec3(1.00, 1.00, 0.10), vec3(1.00, 0.10, 0.00), (t - 0.60) / 0.25);
    } else {
        return mix(vec3(1.00, 0.10, 0.00), vec3(1.00, 1.00, 1.00), (t - 0.85) / 0.15);
    }
}

void main() {
    vec3 src = texture(DiffuseSampler, texCoord).rgb;
    float mask = texture(entityMask, texCoord).r;
    vec4 entityLm = texture(entityLightmap, texCoord);

    // World heat from heuristic color analysis (torches, lava, fire show up here).
    float heat = worldHeatFromColor(src);

    // Entity contribution: every entity fragment is at least "warm body" temperature, with a small lightmap-driven
    // boost so a torchlit entity reads slightly hotter than one in deep shadow.
    float entityHeat = mask * clamp(0.55 + 0.20 * luminance(entityLm.rgb), 0.0, 1.0);
    heat = max(heat, entityHeat);

    // Subtract a touch of source luminance to preserve high-frequency texture detail through the gradient
    // (otherwise hot regions look like flat color blobs).
    heat = clamp(heat - luminance(src) * 0.05, 0.0, 1.0);

    vec3 thermal = thermalGradient(heat);

    // Apply blindness/darkness as a global dimming of the thermal output (matches vanilla behavior).
    float dim = 1.0 - max(blindness, darkness) * 0.7;
    thermal *= dim;

    fragColor = vec4(thermal, 1.0);
}
