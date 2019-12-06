precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
uniform float iGlobalTime;
uniform vec3 brushCol;
float d;

float lookup(vec2 p, float dx, float dy)
{
    vec2 uv = (p.xy + vec2(dx * d, dy * d)) / iResolution.xy;
    vec4 c = texture2D(iChannel0, uv.xy);

	// return as luma
    return 0.2126*c.r + 0.7152*c.g + 0.0722*c.b;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    d = sin(iGlobalTime * 5.0)*0.5 + 1.5; // kernel offset
    vec2 p = fragCoord.xy;

	// simple sobel edge detection
    float gx = 0.0;
    gx += -1.0 * lookup(p, -1.0, -1.0);
    gx += -2.0 * lookup(p, -1.0,  0.0);
    gx += -1.0 * lookup(p, -1.0,  1.0);
    gx +=  1.0 * lookup(p,  1.0, -1.0);
    gx +=  2.0 * lookup(p,  1.0,  0.0);
    gx +=  1.0 * lookup(p,  1.0,  1.0);

    float gy = 0.0;
    gy += -1.0 * lookup(p, -1.0, -1.0);
    gy += -2.0 * lookup(p,  0.0, -1.0);
    gy += -1.0 * lookup(p,  1.0, -1.0);
    gy +=  1.0 * lookup(p, -1.0,  1.0);
    gy +=  2.0 * lookup(p,  0.0,  1.0);
    gy +=  1.0 * lookup(p,  1.0,  1.0);

	// hack: use g^2 to conceal noise in the video
    float g = gx*gx + gy*gy;
    float g2 = g * (clamp(sin(iGlobalTime) / 2.0, 0.8, 1.0));

    vec3 glowColor = brushCol;
    glowColor.r *= g2;
    glowColor.g *= g2;
    glowColor.b *= g2;

    vec4 col = texture2D(iChannel0, p / iResolution.xy);
    col += vec4(glowColor, 1.0);

    fragColor = col;
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}