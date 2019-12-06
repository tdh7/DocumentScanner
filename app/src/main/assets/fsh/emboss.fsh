precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
void mainImage( out vec4 fragColor, in vec2 fragCoord ){
    vec2 uv = fragCoord.xy / iResolution.xy;
    //uv = vec2(uv.x, 1.-uv.y);
    float x = iResolution.x;
    float y = iResolution.y;
    vec3 irgb = texture2D( iChannel0, uv ).rgb;
    vec2 stp0 = vec2(1./x, 0. );
    vec2 stpp = vec2(-1./x, -1./y);
    vec3 c00 = texture2D( iChannel0, uv ).rgb;
    vec3 cp1p1 = texture2D( iChannel0, uv + stpp ).rgb;
    vec3 diffs = (c00 - cp1p1)*1.; // vector difference
    float max = diffs.r;
    if ( abs(diffs.g) > abs(max) ) max = diffs.g;
    if ( abs(diffs.b) > abs(max) ) max = diffs.b;
    float gray = clamp( max + .6, 0., 1. );
    vec3 color = vec3( gray, gray, gray );
    fragColor = vec4( color, 1. );
}
void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}
