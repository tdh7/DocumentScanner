precision mediump float;
uniform vec2                iResolution;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                vTextureCoord;
uniform float iSize;

#define BLUR .7

void mainImage(out vec4 c, vec2 p)
{
    vec2 u = p.xy / iResolution.xy;
    c = mix(texture2D(iChannel1, u), texture2D(iChannel0, u), clamp(BLUR + iSize * 0.25 / 36.0,0.,1.-1e-2));
}

void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}