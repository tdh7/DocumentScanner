precision mediump float;
varying vec2 vTextureCoord;
uniform vec2 iResolution;
uniform sampler2D iChannel0;
vec4 W = vec4(0.2125, 0.7154, 0.0721, 1.0);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;
    
    vec3 origin = texture2D(iChannel0, uv / iResolution.xy).rgb;
    
    float m1_1 = dot(texture2D(iChannel0, uv / iResolution.xy), W);
    float m0_0 = dot(texture2D(iChannel0, (uv + vec2(-1, -1)) / iResolution.xy), W);
    float m0_1 = dot(texture2D(iChannel0, (uv + vec2(-1, 0)) / iResolution.xy), W);
    float m0_2 = dot(texture2D(iChannel0, (uv + vec2(-1, 1)) / iResolution.xy), W);
    float m1_0 = dot(texture2D(iChannel0, (uv + vec2(0, -1)) / iResolution.xy), W);
    float m1_2 = dot(texture2D(iChannel0, (uv + vec2(0, 1)) / iResolution.xy), W);
    float m2_0 = dot(texture2D(iChannel0, (uv + vec2(1, -1)) / iResolution.xy), W);
    float m2_1 = dot(texture2D(iChannel0, (uv + vec2(1, 0)) / iResolution.xy), W);
    float m2_2 = dot(texture2D(iChannel0, (uv + vec2(1, 1)) / iResolution.xy), W);

    float h = -m0_0 - 2. * m0_1 - m0_2 + m2_0 + 2. * m2_1 + m2_2;
    float v = -m0_0 - 2. * m1_0 - m2_0 + m0_2 + 2. * m1_2 + m2_2;
    
    float mag = 1. - length(vec2(h, v));
    
    fragColor = vec4(mix(origin, vec3(mag), 1.0),1.0);
}
void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}