#version 150

uniform float GameTime;

in vec4 vertexColor;
out vec4 fragColor;

void main() {
    // Subtle pulse so the selection reads as alive without strobing. GameTime is a 0..1 cycle over 24000 ticks
    // (one in-game day). Multiplying by 24000 brings it back to ticks; another factor pegs the pulse to roughly
    // 1.5 Hz at 20 tps (24000 * 1.5 / 20 = 1800 cycles per day; phase-multiplier = 1800 * 2pi).
    float pulse = 0.78 + 0.22 * sin(GameTime * 11309.7);
    fragColor = vec4(vertexColor.rgb, vertexColor.a * pulse);
}
