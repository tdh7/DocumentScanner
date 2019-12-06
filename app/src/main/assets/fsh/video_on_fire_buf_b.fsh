precision mediump float;

uniform vec2                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                vTextureCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 px = 1.5 / vec2(640.0,360.0);
	vec2 uv = fragCoord.xy / iResolution.xy;
    vec4 tx = texture2D(iChannel1,uv);
    float dist = distance(tx,texture2D(iChannel1,uv+px));
    px.y *= -1.0;
    dist += distance(tx,texture2D(iChannel1,uv+px));
    px.x *= -1.0;
    dist += distance(tx,texture2D(iChannel1,uv+px));
    px.y *= -1.0;
    dist += distance(tx,texture2D(iChannel1,uv+px));
    uv *= mat2(0.99,0.01,-0.001,0.99);
	fragColor = texture2D(iChannel0,uv+0.002)*vec4(0.91,0.847,0.0,0.0)+
        vec4(smoothstep(0.3,0.8,dist),smoothstep(0.3,1.4,dist),0.0,1.0)*.175;
}


void main() {
    mainImage(gl_FragColor, vTextureCoord * iResolution);
}