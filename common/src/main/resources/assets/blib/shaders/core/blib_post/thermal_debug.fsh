#version 150

// Debug visualization of the captured MRT auxiliary attachments. Selects one channel via debugMode and writes it as
// the screen contents. Modes correspond to BLibThermalDebugState constants — keep them in sync.

uniform sampler2D DiffuseSampler;
uniform sampler2D entityMask;
uniform sampler2D entityLightmap;
uniform sampler2D entityNormal;
uniform sampler2D entityThermalData;
uniform sampler2D entitySpecular;
uniform sampler2D entityMaterialId;

uniform int debugMode;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 src = texture(DiffuseSampler, texCoord);
    float mask = texture(entityMask, texCoord).r;
    vec4 lightmap = texture(entityLightmap, texCoord);
    vec4 normalPacked = texture(entityNormal, texCoord);
    vec4 thermal = texture(entityThermalData, texCoord);
    vec4 specular = texture(entitySpecular, texCoord);
    float matId = texture(entityMaterialId, texCoord).r;

    vec3 outRgb = src.rgb;

    if (debugMode == 1) {
        outRgb = vec3(mask);
    } else if (debugMode == 2) {
        outRgb = lightmap.rgb;
    } else if (debugMode == 3) {
        // Show packed normal as-is so cube faces read as distinct constant colors.
        outRgb = normalPacked.rgb;
    } else if (debugMode == 4) {
        outRgb = vec3(thermal.r);
    } else if (debugMode == 5) {
        outRgb = vec3(thermal.g);
    } else if (debugMode == 6) {
        outRgb = vec3(thermal.b);
    } else if (debugMode == 7) {
        outRgb = vec3(thermal.a);
    } else if (debugMode == 8) {
        outRgb = vec3(specular.g);
    } else if (debugMode == 9) {
        outRgb = vec3(specular.a);
    } else if (debugMode == 10) {
        outRgb = vec3(matId);
    }

    fragColor = vec4(outRgb, 1.0);
}
