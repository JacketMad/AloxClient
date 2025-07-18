#version 120

uniform float offset;
uniform vec2 strength;
uniform float speed;
uniform int maxColors;
uniform vec4 colors[9];
uniform sampler2D font_texture;

// TODO: Fix Random Stutter
void main() {
    vec4 texColor = texture2D(font_texture, gl_TexCoord[0].st);

    if (texColor.a == 0.0)
        discard;

    vec2 pos = gl_FragCoord.xy * strength;
    float param = mod(pos.x + pos.y + offset * speed, 1.0);

    // Divide the range [0, 1] based on maxColors
    float segment = 1.0 / float(maxColors);
    float index = param / segment;
    float frac = mod(index, 1.0);

    float idx1 = floor(index);
    float idx2 = idx1 + 1.0;

    idx1 = mod(idx1, float(maxColors));
    idx2 = mod(idx2, float(maxColors));

    vec4 gradientColor = mix(colors[int(idx1)], colors[int(idx2)], frac);

    gl_FragColor = vec4(gradientColor.rgb * texColor.rgb, texColor.a);
}